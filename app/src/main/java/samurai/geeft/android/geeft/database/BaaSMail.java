package samurai.geeft.android.geeft.database;

import android.os.AsyncTask;
import android.view.View;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Created by ugookeadu on 27/03/16.
 */
public class BaaSMail extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getSimpleName() ;
    private final String mSender;
    private final String mP;
    private final String mReceiver;
    private final int mCode;
    View mView;
    public BaaSMail(String sender,String p,String receiver, int code){
        mSender = sender;
        mP = p;
        mReceiver = receiver;
        mCode = code;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return sendEmail();
    }
    public boolean sendEmail(){
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator(mSender, mP));
            email.setSSLOnConnect(true);
            email.setFrom(mSender);
            email.setSubject("Geeft: confirm mail");
            email.setMsg("Confirm your mail.\nThe code is: "+mCode);
            email.addTo(mReceiver);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

        return true;
    }

}
