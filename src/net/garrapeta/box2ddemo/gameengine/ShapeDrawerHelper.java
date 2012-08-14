package net.garrapeta.box2ddemo.gameengine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

/**
 * This class is a helper for drawing shapes into the canvas *
 */
public class ShapeDrawerHelper {

    /**
     * Draw the shape into the canvas using the passed paint.
     * 
     * @param canvas
     * @param paint
     * @param shape
     */
    public static void draw(Canvas canvas, Paint paint, Shape shape) {
        Type t = shape.getType();

        switch (t) {
        case Circle:
            drawCircleShape(canvas, paint, (CircleShape) shape);
            break;
        case Polygon:
            drawPolygonShape(canvas, paint, (PolygonShape) shape);
            break;
        default:
            throw new IllegalArgumentException("Can not draw shape: " + shape);
        }
    }

    /**
     * Draws a circle shape...
     * @param canvas
     * @param paint
     * @param shape
     */
    private static void drawCircleShape(Canvas canvas, Paint paint, CircleShape shape) {
        float radius = PointsConversionUtils.getInstance().metersToPixels(shape.getRadius());
        canvas.drawCircle(0, 0, radius, paint);
        
        canvas.drawLine(0, 0, radius, 0, paint);
    }

    /**
     * Draws a polygon shape (edges included)
     * @param canvas
     * @param paint
     * @param polygon
     */
    private static void drawPolygonShape(Canvas canvas, Paint paint, PolygonShape polygon) {
        
        int count = polygon.getVertexCount();
        PointF[] aux = new PointF[count];
        for (int i = 0; i < count; i++) {
            Vector2 vertex = new Vector2();
            polygon.getVertex(i, vertex);
            PointF screenPos = new PointF(PointsConversionUtils.getInstance()
                    .metersToPixels(vertex.x), -PointsConversionUtils
                    .getInstance().metersToPixels(vertex.y));
            aux[i] = screenPos;
        }

        drawPath(canvas, paint, aux, true);
    }

    private static void drawPath(Canvas canvas, Paint paint, PointF[] vertexes, boolean close) {

        Path path = new Path();
        for (PointF sreenPoint : vertexes) {
            if (path.isEmpty()) {
                path.moveTo(sreenPoint.x, sreenPoint.y);
            }
            path.lineTo(sreenPoint.x, sreenPoint.y);
        }

        if (close) {
            path.close();
        }

        canvas.drawPath(path, paint);
    }
}
