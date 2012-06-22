package com.emerginggames.snappers.transport;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 12:02
 */
public interface JsonResponseHandler {
    void onOk(Object responce);
    void onError(Exception error);
}
