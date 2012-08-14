package net.garrapeta.box2ddemo.gameengine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View to render the game over
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    
    private static final String LOG_SRC = GameWorld.LOG_SRC + "." + GameView.class.getSimpleName();

    /**  SurfaceView Holder  */
    private SurfaceHolder holder;

    private GameViewListener mGameViewListener;

    /**
     * Constructor
     * @param context
     * @param attrs 
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.holder = getHolder();
        this.holder.addCallback(this);
        
        setFocusableInTouchMode(true);
        requestFocus();
    }

    /**
     * Paint the passed world at its current state
     * @param world
     */
    public final void draw(GameWorld world, float currentFps) {
        // We do a synchronous drawing with this.
        // This is blocking, and it is not handled by Android mechanism
        // to draw the views at all.
        if (holder != null) {
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                world.drawFrame(canvas, currentFps);
                holder.unlockCanvasAndPost(canvas); 
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(LOG_SRC, "surfaceChanged (" + width + ", " + height +")");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(LOG_SRC, "surfaceCreated (" + getWidth() + ", " + getHeight() +")");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(LOG_SRC, "surfaceDestroyed");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mGameViewListener != null) {
            mGameViewListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
    
    public void setGameViewListener(GameViewListener listener) {
        mGameViewListener = listener;
    }

    /**
     * Listener to be notified of when the view changes its size.
     */
    public interface GameViewListener {
        
        /**
         * Called when the view size changes
         * @param w
         * @param h
         * @param oldw
         * @param oldh
         */
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

}
