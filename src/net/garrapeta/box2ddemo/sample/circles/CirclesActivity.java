package net.garrapeta.box2ddemo.sample.circles;

import net.garrapeta.box2ddemo.R;
import net.garrapeta.box2ddemo.gameengine.GameView;
import net.garrapeta.box2ddemo.gameengine.GameView.GameViewListener;
import net.garrapeta.box2ddemo.gameengine.GameWorld;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Simple demo in which you can add circles by touching the screen.
 * They are governed by the gravity, that is in turn governed by the orientation sensor 
 * of the phone.
 */
public class CirclesActivity extends Activity implements GameViewListener {

    /** Log source */
    static final String LOG_SRC = CirclesActivity.class.getSimpleName();

    /** Game world */
    private CirclesWorld mWorld;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
    }

    /**
     * Inits the activity
     */
    private void init() {
        Log.i(LOG_SRC, "Initting...");
        try {
            GameWorld.loadGdxLibrary();
            setContentView(R.layout.game);
            GameView gameView = (GameView) findViewById(R.id.game_view);
            gameView.setGameViewListener(this);
            mWorld = new CirclesWorld(this, gameView);
        } catch (Exception e)  {
            e.printStackTrace();
            String errorMsg = "Goodbye: " + e.getMessage();
            Log.e(LOG_SRC, errorMsg);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWorld.init(w, h);
        mWorld.start();
    }

    //TODO: write the onPause(), etc methods to sync the gameloop to the life cycle of the
    //       activity

}