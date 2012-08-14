package net.garrapeta.box2ddemo.gameengine;

/**
 * Message to be processed at a certain stage in the Game Loop.
 * 
 * There are some operations, such as creating bodies, actors, etc that cannot be
 * performed in the middle of a drawing o simulating operation.
 * 
 * We use a queue of messages that handles them.
 *
 */
public abstract class GameMessage {

    private int mPriority;
    
    static final int MESSAGE_PRIORITY_DEFAULT = 0;
    static final int MESSAGE_PRIORITY_MAX = Integer.MIN_VALUE;
    
    public GameMessage() {
        this(MESSAGE_PRIORITY_DEFAULT);
    }

    public GameMessage(int priority) {
        mPriority = priority;
    }
    
    final int getPriority() {
        return mPriority;
    }
    
    /**
     * To be implemented by subclasses.
     * @param world
     */
    public abstract void process(GameWorld world);

}
