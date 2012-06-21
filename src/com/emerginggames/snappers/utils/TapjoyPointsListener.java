package com.emerginggames.snappers.utils;

import android.content.Context;
import com.emerginggames.snappers.UserPreferences;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyEarnedPointsNotifier;
import com.tapjoy.TapjoyNotifier;
import com.tapjoy.TapjoySpendPointsNotifier;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 12.04.12
 * Time: 19:13
 */
public class TapjoyPointsListener implements TapjoySpendPointsNotifier, TapjoyEarnedPointsNotifier, TapjoyNotifier {
    Context context;
    UserPreferences prefs;

    /*    int unsentPoints = 0;
 int sendingPoints = 0;
 boolean sendingSpentPoints = false;*/

    public TapjoyPointsListener(Context context) {
        this.prefs = UserPreferences.getInstance(context);
    }



    @Override
    public void getUpdatePoints(String currencyName, int pointTotal) {
        if (pointTotal == 0)
            return;
        prefs.addHints(pointTotal);
        TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(pointTotal, this);
    }

    @Override
    public void getUpdatePointsFailed(String error) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void earnedTapPoints(int amount) {
        prefs.addHints(amount);
        TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(amount, this);
    }

    @Override
    public void getSpendPointsResponse(String currencyName, int pointTotal) {
/*        synchronized (this) {
            prefs.setSpentUnsentTapjoyHints(0);
            prefs.setTapjoyHints(pointTotal);
            sendingPoints = 0;
        }
        sendSpent();*/
    }

    @Override
    public void getSpendPointsResponseFailed(String error) {
/*        synchronized (this) {
            prefs.setSpentUnsentTapjoyHints(sendingPoints);
            unsentPoints = sendingPoints;
            sendingPoints = 0;
        }
        sendSpent();*/
    }

/*    public void spendHints(int hints) {
        synchronized (this) {
            if (sendingSpentPoints) {
                unsentPoints += hints;
                return;
            }

            if (sendingPoints != 0)
                throw new RuntimeException("sendingPoints should be zero here!");
            if (unsentPoints != 0)
                throw new RuntimeException("unsentPoints should be zero here!");
            sendingSpentPoints = true;
            unsentPoints = prefs.getSpentUnsentTapjoyHints() + hints;
        }
        sendSpent();
    }

    private void sendSpent() {
        synchronized (this) {
            if (unsentPoints == 0) {
                sendingSpentPoints = false;
                return;
            }
            sendingPoints = unsentPoints;
            unsentPoints = 0;
            TapjoyConnect.getTapjoyConnectInstance().spendTapPoints(sendingPoints, this);
        }
    }*/
}
