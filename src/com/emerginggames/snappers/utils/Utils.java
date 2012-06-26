package com.emerginggames.snappers.utils;

import android.content.Context;
import android.content.res.Resources;
import com.emerginggames.snappers.R;
import com.emerginggames.snappers.data.FriendTable;
import com.emerginggames.snappers.model.FacebookFriend;
import com.emerginggames.snappers.model.SyncData;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 26.06.12
 * Time: 6:42
 */
public class Utils {

    public static String getGiftsMessage(Context context, SyncData data){
        FacebookFriend[] friends = FriendTable.getFriends(context, data.gifts);
        Resources res = context.getResources();
        String message;
        int size = friends == null ? 0 : friends.length;
        boolean more = false;
        String source;
        switch (size){
            case 0:
                source = res.getString(R.string.someone);
                break;
            case 1:
                source = friends[0].first_name;
                break;
            case 2:
                source = res.getString(R.string.twoFriends, friends[0].first_name, friends[1].first_name);
                break;
            default:
                more = true;
                source = String.format("%s, %s", friends[0].first_name, friends[1].first_name);
        }

        if (data.gifts.length == 1)
            message = res.getString(R.string.receivedGift, source);
        else if (!more)
            message = res.getString(R.string.receivedGifts, data.gifts.length, source);
        else
            message = res.getString(R.string.receivedGiftsMore, data.gifts.length, source);

        return message;
    }
}
