package net.garrapeta.box2ddemo.gameengine;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Interface of actor.
 * Everything in the games that moves, shoots and jumps is an actor
 *
 */
public interface Actor {

    /**
     * Draw the actor in the passed canvas
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint);

}
