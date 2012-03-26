package ru.emerginggames.snappers;

import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.modifier.AlphaModifier;
import com.e3roid.drawable.modifier.ParallelModifier;
import com.e3roid.drawable.modifier.ProgressModifier;
import com.e3roid.drawable.modifier.ScaleModifier;
import com.e3roid.drawable.modifier.function.Linear;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.Texture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.event.SceneUpdateListener;
import ru.emerginggames.snappers.sprites.OutlinedTextSprite;

public class SplashScreenActivity extends E3Activity implements SceneUpdateListener {

	private final static int WIDTH  = 480;
	private final static int HEIGHT = 800;
	
	private final static int SPLASH_MSEC = 3000;
	
	private Sprite  logo;
	private Texture logoTexture;
    private OutlinedTextSprite label;
    private TiledTexture eyesTexture;
    private TiledTexture eyesShadowTexture;
    private AnimatedSprite eyesSprite;
    private AnimatedSprite eyesShadowSprite;

	@Override
	public E3Engine onLoadEngine() {
        Display display = getWindowManager().getDefaultDisplay();
		//E3Engine engine = new E3Engine(this, WIDTH, HEIGHT);
		E3Engine engine = new E3Engine(this, display.getWidth(), display.getHeight());
		engine.requestFullScreen();
        engine.requestPortrait();
		return engine;
	}

	@Override
	public E3Scene onLoadScene() {
		E3Scene scene = new E3Scene();
		
		// start next activity after waiting 3 seconds.
		scene.registerUpdateListener(SPLASH_MSEC, this);
		
		int centerX = (getWidth()  - logoTexture.getWidth())  / 2;
		int centerY = (getHeight() - logoTexture.getHeight()) / 2;
		
		logo = new Sprite(logoTexture, centerX, centerY);
		
		// show logo in 3 seconds and scale logo in 1 seconds.
		ParallelModifier modifier = new ParallelModifier(
				new ProgressModifier(new AlphaModifier(0, 0, 1), 3000, Linear.getInstance()),
				new ProgressModifier(new ScaleModifier(0, 0.8f, 1.0f), 1000, Linear.getInstance()));
		logo.addModifier(modifier);
		
		scene.getTopLayer().add(logo);
		scene.setBackgroundColor(0, 1f, 1f);

        label.move((getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 3 * 2);

        scene.getTopLayer().add(label);

        return scene;
	}

	@Override
	public void onLoadResources() {
		logoTexture = new AssetTexture("logo.png", this);
        Typeface fnt = Typeface.createFromAsset(getAssets(), "shag_lounge.otf");
        label = new OutlinedTextSprite("Loading...",  50, Color.WHITE, Color.BLACK, Color.TRANSPARENT, fnt, this);

	}

	@Override
	public void onUpdateScene(E3Scene scene, long elapsedMsec) {
		scene.unregisterUpdateListener(this);
		startActivity(new Intent(this, GameActivityE3.class));
		finish();
	}

}
