package net.garrapeta.box2ddemo.gameengine.actor;

import net.garrapeta.box2ddemo.gameengine.Actor;
import net.garrapeta.box2ddemo.gameengine.GameWorld;
import net.garrapeta.box2ddemo.gameengine.PointsConversionUtils;
import net.garrapeta.box2ddemo.gameengine.ShapeDrawerHelper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Trivial implementation of an actor that has one physical body.
 * The actor is drawn using the shapes attached to the body. 
 */
public class AbstractSimpleActor implements Actor {

    protected GameWorld mWorld;
    protected Body mBody;
    protected int mColor;

    
    /**
     * Constructor
     * 
     * @param world
     * @param worldPos
     * @param dynamic whether or not the actor has a dynamic body. In Box2d there bodies that
     *                 are not dynamic (fully simulated) but only static or kinetic
     */
    public AbstractSimpleActor(GameWorld world, Vector2 worldPos, boolean dynamic) {
        mWorld = world;
        
        // Create Dynamic Body
        BodyDef bodyDef = new BodyDef();

        bodyDef.position.set(worldPos);

        mBody= world.getBox2dWorld().createBody(bodyDef);

        // TODO: this could be selected by passing argument to the constructor
        mBody.setSleepingAllowed(false);

        mBody.setUserData(this);

        if (dynamic) {
            mBody.setType(BodyType.DynamicBody);
        }
    }

    @Override
    public final void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Style.STROKE);
        paint.setColor(mColor);

        canvas.save();

        // Translation to worldCenter
        Vector2 worldPos = mBody.getWorldCenter();
        PointF screenPos = PointsConversionUtils.getInstance().worldToScreen(worldPos.x, worldPos.y);
        canvas.save();
        canvas.translate(screenPos.x, screenPos.y);

        // Rotation
        canvas.rotate(- (float)Math.toDegrees(mBody.getAngle()));

        // Drawing of shapes
        for (Fixture fixture : mBody.getFixtureList()) {
            Shape shape = fixture.getShape();
            ShapeDrawerHelper.draw(canvas, paint, shape);
        }

        // restoration of translation
        canvas.restore();
    }

}
