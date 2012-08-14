package net.garrapeta.box2ddemo.sample.basic;

import net.garrapeta.box2ddemo.R;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple activity that just has a body falling.
 * The activity does not provide any kind of visual representation but log traces.
 *
 */
public class BasicBox2dActivity extends Activity {

    /** Log source */
    static final String LOG_SRC = BasicBox2dActivity.class.getSimpleName();

    /** Physical world */
    private World mBox2dWorld;
    
    /** Body we are simulating*/
    private Body mBody;
    
    /** For monitoring the postion */
    private TextView mPostionTextView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);
        mPostionTextView = (TextView) findViewById(R.id.positionTextView); 

        
        Log.i(LOG_SRC, "Initting...");
        try {
            loadGdxLibrary();
            createBox2dWorld();
            createPhysicalBody();
            startSimulation();
        } catch (Exception e)  {
            e.printStackTrace();
            String errorMsg = "Goodbye: " + e.getMessage();
            Log.e(LOG_SRC, errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadGdxLibrary() {
        Log.i(LOG_SRC, "Attempting to load gdx native library");
        try {
            System.loadLibrary("gdx");
            Log.i(LOG_SRC, "gdx native library loaded");
        } catch (Throwable t) {
            throw new IllegalStateException("Could not load gdx library");
        }
    }
    
    private void createBox2dWorld() {
        Vector2 gravity = new Vector2(0f, -9.8f);
        mBox2dWorld = new World(gravity, true);
    }
    
    private void createPhysicalBody() {
        // Create Dynamic Body

        // This is the coordinate where the body is initially placed
        Vector2 worldPos = new Vector2(0, 10);

        // We create the body given a BodyDef
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(worldPos);
        mBody = mBox2dWorld.createBody(bodyDef);

        // We set the body to be fully dynamic (it's behaviour will be fully simulated)
        mBody.setType(BodyType.DynamicBody);


        // Right now the body has no shape, no weight, no density...
        // We provide that information giving a Shape and a fixture
        
        // This body will have a circular shape...
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1f);

        // Assign shape to Body creating a fixture.
        // A fixture is the "glue" between a shape and a body.
        // Assign the density of the part of the body contained in the shape, via the fixture
        float density = 1.0f;
        @SuppressWarnings("unused")
        Fixture fixture =  mBody.createFixture(circleShape, density);

        // this seems to dealloc memory used by obscure JNI code... 
        circleShape.dispose();
        
        // Now we have a body with:
        //  - A shape
        //  - it's density
        //    - so box2d can compute the final mass of the body
        //
        //  - we set up a force applied to this body:
        //    - the gravity we created when setting up the world
        //  
        //  - so Box2d can compute the acceleration of the body because of the gravity
        //  - so Box2d can update it's linear velocity as time passes
        //  - so Box2d can update it's position in the space as time passes :-)
    }
    

    
    private void startSimulation() {
        new Thread(new PhysicsThreadRunnable()).start();
    }

    /**
     * Physics Runnable
     */
    class PhysicsThreadRunnable implements Runnable {

        @Override
        public void run() {
            Log.i(LOG_SRC, "Physics thread started");

            // Timestep of the simulation, this SHOULD REMAIN CONSTANT, and 60 Hertz is the recommended.
            // Expressed in seconds, NOT MILLISECONDS. Box2d works with standard units. 
            float timestep = 1 / 60f; 

            while (true) {
                mBox2dWorld.step(timestep, 2, 1);
                monitorPosition();

                try {
                    // For demo purposes we sleep 100 ms. This makes the Box2d to go "low motion"
                    // For a realistic simulation we have to match the rate the Box2d simulation evolves at to
                    // real time.
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }
            
        }

    }
    
    private void monitorPosition() {
        // This activity provides no graphical representation of the body, so you have to read the traces 
        // to see that it is "falling". Boring.
        final String pos = mBody.getWorldCenter().x + ", " + mBody.getWorldCenter().y;
        Log.i(LOG_SRC, "Current position of the body: " + pos);
        
        mPostionTextView.post(new Runnable() {
            @Override
            public void run() {
                mPostionTextView.setText(pos);
            }});
    }
}