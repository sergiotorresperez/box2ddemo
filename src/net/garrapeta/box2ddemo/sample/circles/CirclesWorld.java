package net.garrapeta.box2ddemo.sample.circles;


import net.garrapeta.box2ddemo.gameengine.Actor;
import net.garrapeta.box2ddemo.gameengine.GameMessage;
import net.garrapeta.box2ddemo.gameengine.GameView;
import net.garrapeta.box2ddemo.gameengine.GameWorld;
import net.garrapeta.box2ddemo.gameengine.PointsConversionUtils;
import net.garrapeta.box2ddemo.gameengine.actor.SimpleCircumferenceActor;
import net.garrapeta.box2ddemo.gameengine.actor.SimpleLoopActor;


import com.badlogic.gdx.math.Vector2;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * The world
 */
public class CirclesWorld extends GameWorld implements OnTouchListener, SensorEventListener  {

    // TODO: unregister in onPause, resume in onResume...
    private SensorManager mSensorManager;

    /**
     * Constructor
     */
    public CirclesWorld(Activity activity, GameView gameView) {
        super(gameView);
        gameView.setOnTouchListener(this);
        mSensorManager = (SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void init(int screenWidth, int screenHeight) {
        // initialization of world size
        float worldHeight = 15;
        float pixelsIntMeter = screenHeight / worldHeight;
        float worldWidth = screenWidth / pixelsIntMeter;
        PointsConversionUtils.getInstance().init(pixelsIntMeter, worldWidth, worldHeight);

//        // establishment of gravity
//        setGravity(0f, -9.8f);

        // creation of "walls"
        float m = 0.5f;
        Vector2[] vertex = new Vector2[] {
                new Vector2(m,m),
                new Vector2(m, worldHeight - m),
                new Vector2(worldWidth - m, worldHeight - m),
                new Vector2(worldWidth - m, m),
                new Vector2(m, m)
            };
        addActor(new SimpleLoopActor(this, new Vector2(0,0), false, vertex));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        post(new GameMessage() {
            @Override
            public void process(GameWorld world) {
                if (action == MotionEvent.ACTION_DOWN) {
                    createCircleActor(x, y);
                }           
            }});
        
        return true;
    }

    private void createCircleActor(float screenX, float screenY) {
        Vector2 worldPos = PointsConversionUtils.getInstance().screenToWorld(screenX, screenY);
        Actor actor = new SimpleCircumferenceActor(this, worldPos, true, 0.5f);
        addActor(actor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            this.setGravity(- event.values[2], event.values[1]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
