package net.garrapeta.box2ddemo.gameengine;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * World of the game.
 * All the actors and logic should be handled here
 */
public abstract class GameWorld {

    public static final String LOG_SRC = GameWorld.class.getSimpleName();
    public static final String LOG_SRC_LOOP = LOG_SRC + ".loop";

    /**
     * Box2d physical world.
     * This has nothing to do with actors, just bodies.
     */
    private World mBox2dWorld;

    /** Paint used to draw the world */
    private Paint mPaint;

    /** Game view   */
    private GameView mGameView;
    
    /** Game loop thread */
    private Thread mGameThread;

    /**
     * Queue of messages, to process all the logic of the game in a stage of the
     * game loop
     */
    private List<GameMessage> mMessages;

    /** Whether the game is running (looping) */
    private boolean mRunning = false;

    /** Whether the game is paused */
    private boolean mPaused = false;

    /** FPS of the game */
    private int mFps = 33;

    /** Period of a frame. Time a frame must take. In ms */
    private long mFrameLength = 1000 / mFps;

    /**
     * Seconds that last one step of the physical simulation.
     * This is seconds, not ms, since Box2D works with standard units.
     * THIS HAS TO BE CONSTANT, otherwise the simulation won't be stable and we will
     * have a lot of oddities.
     * 
     * Recommended frequency of the simulation is 60Hz.
     * 
     */
    private float mPhysicStep = 1 / 60f;
    
    /**
     * Due to the fact we want a constant physical step, but the frame rate is variable,
     * we do several physical steps in one frame. This is the maximum we can do.
     */
    private int mMaxPhysicsStepsPerFrame = 5;
    
    /** Current FPS. Actually, FPS achieved last frame */
    private float mCurrentFps;

    /** Gravity vector of the world */
    private Vector2 mGravity;

    /** List of actors */
    private List<Actor> mActors;

    /**
     * Loads native library 
     * TODO: perhaps not the best class to place this method...
     */
    public static void loadGdxLibrary() {
        Log.i(LOG_SRC, "Attempting to load gdx native library");
        try {
            System.loadLibrary("gdx");
            Log.i(LOG_SRC, "gdx native library loaded");
        } catch (Throwable t) {
            throw new IllegalStateException("Could not load gdx library");
        }
    }

    /**
     * Constructor
     */
    public GameWorld(GameView gameView) {
        super();
        mGameView = gameView;
        mPaint = new Paint();
        mActors = new ArrayList<Actor>();
        mMessages = new ArrayList<GameMessage>();
        mGravity = new Vector2(0f, 0f);
        mBox2dWorld = new World(mGravity, true);
        
        // By default, Box2d clears all the forces in the world after step.
        // We are going to call step() several times in one same step, so we
        // disable this and call clearForces() ourselves
        mBox2dWorld.setAutoClearForces(false);
    }

    /**
     * Initialises the world.
     * 
     * @param screenWidth
     * @param screenHeight
     */
    public abstract void init(int screenWidth, int screenHeight);

    /**
     * Set the gravity
     * @param vx
     * @param vy
     */
    public void setGravity(float vx, float vy) {
        mGravity.x = vx;
        mGravity.y = vy;
        mBox2dWorld.setGravity(mGravity);
    }

    /**
     * @return the gravity
     */
    public Vector2 getGravity() {
        return mGravity;
    }

    /**
     * Processes all the enqueued messages. If some logic requires some
     * operation that cannot be done in the middle of a physical simulation or drawing 
     * operation, it's better to avoid synchronized methods and enqueue a message instead, 
     * so it can be processed at one stage of the game loop.
     * 
     * If we synchronize the method we will lose performance.
     * This is similar to what Android does in the UI thread. 
     */
    private void processMessages() {
        GameMessage[] messages;
        synchronized (mMessages) {
            messages = new GameMessage[mMessages.size()];
            mMessages.toArray(messages);
            mMessages.clear();
        }
        for (GameMessage message : messages) {
            message.process(this);
        }
    }
    
    /**
     * Enqueue a message to be processed at the dedicated stage of the game loop.
     * @param message
     */
    public void post(GameMessage message) {
        synchronized (mMessages) {
            int index = 0;
            for (GameMessage aux : mMessages) {
                if (aux.getPriority() > message.getPriority()) {
                    break;
                }
                index++;
            }
            mMessages.add(index, message);
        }
    }

    /**
     * Simulates the physics in the world
     * @param frameTime, time we have to simulate, in milliseconds
     */
    private void simulatePhysics(float frameTime) {
        int steps = mMaxPhysicsStepsPerFrame;

        // Box2d works with seconds
        frameTime = frameTime / 1000;

        // We want a constant physical step, but the framerate is variable.
        // To handle this we do several smaller steps in one frame, until we have
        // covered the full time to simulate.
        while (frameTime > 0 && steps > 0) {
            mBox2dWorld.step(mPhysicStep, 2, 1);
            frameTime -= mPhysicStep;
            steps --;
        }

        mBox2dWorld.clearForces();
    }

    /**
     * Draws the world...
     * 
     * @param canvas
     * @param currentFps
     */
    public final void drawFrame(Canvas canvas, float currentFps) {
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
        drawActors(canvas, mPaint);
        drawDebugInfo(canvas, mPaint, currentFps);
    }

    /**
     * Draws the actors...
     * @param canvas
     * @param paint
     */
    private void drawActors(Canvas canvas, Paint paint) {
        for (Actor actor : mActors) {
            actor.draw(canvas, mPaint);
        }
    }

    /** Draws the debug info */
    private void drawDebugInfo(Canvas canvas, Paint paint, float currentFps) {
        String str = "actors#: " + mActors.size() + " FPS: " + currentFps;

        // TODO: things harcoded here
        paint.setColor(Color.RED);
        paint.setTextSize(20);
        canvas.drawText(str, 0, 20, paint);
    }

    /**
     * Start looping
     */
    public void start() {
        Log.i(LOG_SRC, "Starting game loop...");
        mRunning = true;

        mGameThread = new Thread(new GameLoopRunnable());
        mGameThread.start();

    }

    /**
     * Stop looping (ends the game loop thread)
     */
    public void stop() {
        Log.i(LOG_SRC, "Stopping game loop...");
        mRunning = false;
    }

    /**
     * Pause the game thread (does not exit the game loop, just pauses it)
     */
    public void pause() {
        Log.i(LOG_SRC, "Pausing game loop...");
        if (!mRunning) {
            throw new IllegalStateException("Cannot pause the game while not running");
        }
        synchronized (mGameThread) {
            mPaused = true;
        }
    }

    /**
     * Resume the game thread
     */
    public void resume() {
        Log.i(LOG_SRC, "Resuming game loop...");
        if (!mRunning) {
            throw new IllegalStateException("Cannot resume the game while not running");
        }
        synchronized (mGameThread) {
            mPaused = false;
            mGameThread.notify();
        }
    }

    /**
     * @return if the game is running (has been started)
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * @return if the game is paused
     */
    public boolean isPaused() {
        return mPaused;
    }

    
    /**
     * Adds an actor. 
     * This operation should be done using a message that should be processed in the
     * stage of processing messages.
     * @param actor
     */
    public void addActor(Actor actor) {
        mActors.add(actor);
    }

    /**
     * It is not safe to make the box2d world public...
     * Creation of bodies and physical stuff should be done with the mediation of the GameWorld,
     * so we can have control over WHEN they are created, and we could avoid the creation of
     * a body in the middle of a simulation step....
     * 
     * But this is just a quick sample...
     * 
     * @return the box2d world
     */
    public World getBox2dWorld() {
        return mBox2dWorld;
    }

    /**
     * Game loop runnable
     */
    class GameLoopRunnable implements Runnable {

        @Override
        public void run() {
            Log.i(LOG_SRC_LOOP, "Game loop started");

            float frameTime = 0;

            while (mRunning) {
                long initTimeStamp = System.currentTimeMillis();
                
                processMessages();
                simulatePhysics(frameTime);
                mGameView.draw(GameWorld.this, mCurrentFps);

                // Adjust of the length of this frame, so we match the FPS
                long elapsed = System.currentTimeMillis() - initTimeStamp;
                
                long diff = mFrameLength - elapsed;
                if (diff > 0) {
                    try {
                        Thread.sleep(diff);
                    } catch (InterruptedException ie) {
                    }
                }
                
                frameTime = System.currentTimeMillis() - initTimeStamp;
                Log.v(LOG_SRC_LOOP, "Frame time: " + frameTime);

                mCurrentFps = 1000 / frameTime;

                // The pause mechanism is done by making the thread wait until is 
                // resumed (awaken)
                synchronized (mGameThread) {
                    if (mPaused) {
                        Log.d(LOG_SRC_LOOP, "Game loop paused");
                        try {
                            mGameThread.wait();
                            Log.d(LOG_SRC_LOOP, "Game loop resumed");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            Log.i(LOG_SRC_LOOP, "Draw loop ended");
        }
    }



}
