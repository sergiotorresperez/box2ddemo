package net.garrapeta.box2ddemo.gameengine.actor;

import net.garrapeta.box2ddemo.gameengine.GameWorld;
import android.graphics.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class SimpleEdgeActor extends AbstractSimpleActor {

    
    /**
     * Simple actor defined by a body, attached to a edge shape.
     * An edge is a list of segments, like a Path in Android.
     * 
     * @param world
     * @param worldPos
     * @param dynamic
     * @param vertexes of the edge shape, counter clockwise
     */
    public SimpleEdgeActor(GameWorld world,  Vector2 worldPos, boolean dynamic, Vector2[] vertexes) {
        super(world, worldPos, dynamic);
        
        mColor = Color.GREEN;

        // FIXME: why this does not work if we just create a shape with the vertexes??? This is just a workaround/
        for (int i = 0; i < vertexes.length - 1; i++) {
            Vector2 p0 = vertexes[i];
            Vector2 p1 = vertexes[i + 1];
            
            PolygonShape shape = new PolygonShape();
            shape.set(new Vector2[] {p0, p1});
            mBody.createFixture(shape, 1.0f);

            // We can dispose the shape even if we plan to use the shape later to draw the actor.
            // Box2d keeps a copy of the shape in the JNI guts, so we can ask for that later.
            // This shape is only a wrapper of the C++ stuff.
            shape.dispose();
        }
        
    }
}
