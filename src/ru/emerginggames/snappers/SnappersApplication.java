package ru.emerginggames.snappers;

import android.app.Application;
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
        mode = ReportingInteractionMode.NOTIFICATION,
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
    @Override
    public void onCreate() {
        if (Settings.CRASH_REPORTER == Settings.CrashReporter.ACRA)
            ACRA.init(this);
        super.onCreate();
        if (Settings.CRASH_REPORTER == Settings.CrashReporter.ACRA)
            ErrorReporter.getInstance().putCustomData("git_commit", Settings.getGitCommitString(getApplicationContext()));
    }
}
