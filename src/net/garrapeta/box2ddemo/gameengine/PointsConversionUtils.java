package net.garrapeta.box2ddemo.gameengine;

import com.badlogic.gdx.math.Vector2;

import android.graphics.Point;
import android.graphics.PointF;

/**
 * In Android we work with pixels or dp.
 * Box2d works with meters (actually it works with "world units", but this world units should
 * be larger that a pixel. A world of 640 * 480 "world units" in Box2d is too big. Units should
 * be bigger.)
 * 
 * In Android the point 0,0 is the upper left corner. In Box2d it is the bottom left corner.
 * 
 * In Android we prefer to use PointF, Box2d uses Vector2.
 * 
 * This class is handy for doing those conversions. 
 *
 */
public class PointsConversionUtils {

    /**  Ratio of pixels / meters  */
    private float mPixelsInMeter;

    /** For implementing a singleton */
    private static PointsConversionUtils mInstance;
    @SuppressWarnings("unused")
    private float mWorldWidth;
    private float mWorldHeight;

    /**
     * @return gets a singleton of this class
     */
    public static PointsConversionUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PointsConversionUtils();
        }
        return mInstance;
    }

    /**
     * Initialisation
     * @param pixelsInMeter
     * @param worldWidth
     * @param worldHeight
     */
    public void init(float pixelsInMeter, float worldWidth, float worldHeight) {
        mPixelsInMeter = pixelsInMeter;
        mWorldWidth = worldWidth;
        mWorldHeight = worldHeight;
    }

    // TODO: javadoc bla bla bla

    public float pixelsToMeters(float pixels) {
        return pixels / mPixelsInMeter;
    }
    
    public float metersToPixels(float meters) {
        return meters * mPixelsInMeter;
    }

    public PointF worldToScreen(Vector2 worldPos) {
        PointF screenPos = worldToScreen(worldPos.x, worldPos.y);
        return screenPos;
    }

    public PointF worldToScreen(float worldX, float worldY) {
        PointF screenPos = new PointF(metersToPixels(worldX), metersToPixels(mWorldHeight - worldY));
        return screenPos;
    }

    public Vector2 screenToWorld(Point screenPos) {
        Vector2 worldPos = new Vector2(screenPos.x, screenPos.y);
        return worldPos;
    }
    
    public Vector2 screenToWorld(float screenX, float screenY) {
        Vector2 worldPos = new Vector2(pixelsToMeters(screenX), mWorldHeight - pixelsToMeters(screenY));
        return worldPos;
    }
}
