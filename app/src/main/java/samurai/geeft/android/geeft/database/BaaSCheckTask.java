package samurai.geeft.android.geeft.database;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by daniele on 25/02/16.
 */
abstract class BaaSCheckTask extends AsyncTask<Void,Void,Boolean>{

    int mResultToken;
    //-------------------Macros
     final int RESULT_OK = 1;
     final int RESULT_FAILED = 0;
     final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public boolean checkError(BaasResult<?> baasResult){
        if (baasResult.isSuccess()) {
            return true;
        } else {
            if(baasResult.error() instanceof BaasInvalidSessionException){
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else {
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

}
