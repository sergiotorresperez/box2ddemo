package net.garrapeta.box2ddemo.gameengine.actor;


import net.garrapeta.box2ddemo.sample.circles.CirclesWorld;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;

/**
 * Actor defined by just one physical body, that has just one circunference as shape. *
 */
public class SimpleCircunferenceActor extends AbstractSimpleActor {

    public SimpleCircunferenceActor(CirclesWorld world, Vector2 worldPos, boolean dynamic, float radius) {
        super(world, worldPos, dynamic);

        mColor = Color.RED;
        
        // Create Shape with Properties
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        // Assign shape to Body
        mBody.createFixture(circleShape, 1.0f);
        
        // We can dispose the shape even if we plan to use the shape later to draw the actor.
        // Box2d keeps a copy of the shape in the JNI guts, so we can ask for that later.
        // This shape is only a wrapper of the C++ stuff.
        circleShape.dispose();
    }
    

}
