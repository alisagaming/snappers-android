package ru.emerginggames.snappers.transport;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 18.06.12
 * Time: 22:30
 */
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


import javax.net.ssl.X509TrustManager;


public class TrustAllManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] cert, String authType)
            throws CertificateException {
    }


    public void checkServerTrusted(X509Certificate[] cert, String authType)
            throws CertificateException {
    }


    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}