package net.garrapeta.box2ddemo.gameengine.actor;

import net.garrapeta.box2ddemo.gameengine.GameWorld;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;

/**
 * Simple actor defined by a body, attached to a loop shape.
 * An loop is a list of segments, like a Path in Android.
 * 
 * @param world
 * @param worldPos
 * @param dynamic
 * @param vertexes of the edge shape, counter clockwise
 */
public class SimpleLoopActor extends AbstractSimpleActor {
    

    public SimpleLoopActor(GameWorld world,  Vector2 worldPos, boolean dynamic, Vector2[] vertexes) {
        super(world, worldPos, dynamic);
        
        mColor = Color.GREEN;

        mColor = Color.GREEN;

        // Create Shape with Properties
        ChainShape chainShape  = new ChainShape();
        chainShape.createLoop(vertexes);
        mBody.createFixture(chainShape, 1.0f);
        chainShape.dispose();        
    }
}
