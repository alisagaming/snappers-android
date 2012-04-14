package ru.emerginggames.snappers;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;


/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 11.04.12
 * Time: 18:30
 */

@ReportsCrashes(formKey = "dE9Tby0xc2puWlBCXzhJb3lpQWxGTWc6MQ",
        /*formUri = "http://www.bugsense.com/api/acra?api_key=8a555912",*/
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resNotifTickerText = R.string.crash_notif_ticker_text,
        resNotifTitle = R.string.crash_notif_title,
        resNotifText = R.string.crash_notif_text,
        resNotifIcon = android.R.drawable.stat_notify_error, // optional. default is a warning sign
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class SnappersApplication extends Application {
    private boolean isActivityActive;
    private boolean isSwitchingActivity;
    private boolean isScreenOn = true;
    private boolean isUnlocked = true;
    private boolean isMusicEnabled;
    private Activity currentActivity;
    private Receiver screenReceiver;

    @Override
    public void onCreate() {
        if (Settings.CRASH_REPORTER == Settings.CrashReporter.ACRA)
            ACRA.init(this);
        super.onCreate();
        if (Settings.CRASH_REPORTER == Settings.CrashReporter.ACRA)
            ErrorReporter.getInstance().putCustomData("git_commit", Settings.getGitCommitString(getApplicationContext()));
    }

    public void musicStatusChanged(){
        isMusicEnabled = UserPreferences.getInstance(getApplicationContext()).getMusic();
        if (isMusicEnabled){
            if (screenReceiver == null)
                screenReceiver = new Receiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getApplicationContext().registerReceiver(screenReceiver, filter);
            startMusicifShould();
        } else{
            if (screenReceiver != null)
                unregisterReceiver(screenReceiver);
            stopMusic();
        }
    }

    public void activityPaused() {
        isActivityActive = false;
        if (!isSwitchingActivity)
            stopMusic();
    }

    public void activityResumed(Activity activity) {
        if (currentActivity == null)
            musicStatusChanged();
        currentActivity = activity;
        isActivityActive = true;
        isSwitchingActivity = false;
        startMusicifShould();
    }

    public void setSwitchingActivities() {
        isSwitchingActivity = true;
    }

    private void startMusicifShould(){
        if (isMusicEnabled && currentActivity != null && isScreenOn && isUnlocked && isActivityActive)
            SoundManager.getInstance(currentActivity).startMusic();
    }

    private void stopMusic(){
        if (currentActivity != null)
            SoundManager.getInstance(currentActivity).stopMusic();
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                isUnlocked = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
            }
            else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                isUnlocked = true;
                startMusicifShould();
            }

        }
    }
}
