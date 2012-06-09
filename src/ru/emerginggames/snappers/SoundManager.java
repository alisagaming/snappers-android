package ru.emerginggames.snappers;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 08.04.12
 * Time: 8:24
 */
public class SoundManager {
    private static SoundManager instance;
    private Activity context;
    SoundPool soundPool;
    int buttonSoundId = 0;
    MediaPlayer mediaPlayer;
    //boolean continuePlayingFlag;

    public SoundManager(Activity context) {
        this.context = context;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("sounds/button2g.mp3");
            buttonSoundId = soundPool.load(descriptor, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            AssetFileDescriptor afd = context.getAssets().openFd("sounds/soundtrack.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (Exception e) {
        }
    }

    public static SoundManager getInstance(Activity context) {
        if (instance == null)
            instance = new SoundManager(context);
        else
            instance.context = context;

        return instance;
    }

    public void setUp() {
        context.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void playButtonSound() {
        if (UserPreferences.getInstance(context).getSound())
            soundPool.play(buttonSoundId, 1.0f, 1.0f, 0, 0, 1);
    }

    public void startMusic() {
        if (mediaPlayer.isPlaying())
            return;
        try {
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.6f, 0.6f);
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    public static void playButtonSoundIfPossible(){
        if (instance!= null)
            instance.playButtonSound();
    }
}
