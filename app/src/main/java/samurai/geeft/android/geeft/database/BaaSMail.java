package samurai.geeft.android.geeft.database;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonObject;

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
    private String mP;
    private final String mReceiver;
    private final int mCode;
    View mView;
    public BaaSMail(String sender,String receiver, int code){
        mSender = sender;
        mReceiver = receiver;
        mCode = code;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return sendEmail();
    }
    public boolean sendEmail(){
        try {

            mP = getTimeFields();
            if(mP == null)
                return false;
            else {
                Email email = new SimpleEmail();
                email.setHostName("smtp.gmail.com");
                email.setSmtpPort(587);
                email.setAuthenticator(new DefaultAuthenticator(mSender, mP));
                email.setSSLOnConnect(true);
                email.setFrom(mSender);
                email.setSubject("Geeft: confirm mail");
                email.setMsg("Confirm your mail.\nThe code is: " + mCode);
                email.addTo(mReceiver);
                email.send();
            }
        } catch (EmailException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String getTimeFields() {
        String url = "/plugin/get.timeFields";
        BaasResult<JsonObject> result = BaasBox.rest().sync(Rest.Method.GET, url);
        String timeField;
        try {
            timeField = result.get().getObject("data").getString("timeField");
        } catch (BaasException e) {
            timeField = null;
            e.printStackTrace();
        }
        return timeField;
    }

}
