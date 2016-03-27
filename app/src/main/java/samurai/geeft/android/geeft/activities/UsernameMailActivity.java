package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUpdateUserFeedback;
import samurai.geeft.android.geeft.database.BaaSUpdateUsernameMail;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;

/**
 * Created by joseph on 25/03/16.
 */
public class UsernameMailActivity extends AppCompatActivity implements TaskCallbackBooleanToken {

    private final String TAG = getClass().getSimpleName();
    private EditText mNickname,mEmail;
    private Button mButtonDone;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private ProgressDialog mProgressDialog;
    //-------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_mail);

        //TODO: Just for testing REMOVE
        Log.i("USERNAMEMAIL", "Inside UsernameMailActivity after inflating the layout.");

        mNickname=(EditText) findViewById(R.id.username_edittext);
        mEmail=(EditText) findViewById(R.id.email_edittext);
        mButtonDone=(Button) findViewById(R.id.username_request_button);
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("USERNAMEMAIL", "Nickname"+mNickname.getText().toString()+"!");
                Log.i("USERNAMEMAIL", "Email"+mEmail.getText().toString()+"!");
                if ((mNickname.getText().toString().isEmpty())||(mNickname.getText().toString() == null)) {
                    Toast.makeText(UsernameMailActivity.this, R.string.no_valid_username_toast, Toast.LENGTH_SHORT).show();
                } else {
                    String email = mEmail.getText().toString();
                    if (isValidEmail(email)) {
//                        Set mail and username
                        Log.i("USERNAMEMAIL", "Valid mail.");
                        final String myName = BaasUser.current().getName().toString();
                        new BaaSUpdateUsernameMail(getApplicationContext(),myName,
                                mNickname.getText().toString(),mEmail.getText().toString(),UsernameMailActivity.this).execute();
                        mProgressDialog = ProgressDialog.show(UsernameMailActivity.this,"Attendere"
                                ,"Salvataggio in corso");
                        startMainActivity();
                    } else {
                        Toast.makeText(UsernameMailActivity.this, R.string.no_valid_mail_toast, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    public void done(boolean result,int resultToken){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(result) {
            Toast.makeText(this, "Successo", Toast.LENGTH_SHORT);
        }
        else{
            if(resultToken == RESULT_SESSION_EXPIRED){
                Log.e(TAG,"Invalid Session token");
                startLoginActivity();
            }
            else{
                Log.e(TAG,"Error occured");
                new AlertDialog.Builder(UsernameMailActivity.this)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //Starts the MainActivity
    private void startMainActivity(){
        final Intent mMainIntent = new Intent(UsernameMailActivity.this,
                MainActivity.class);
        startActivity(mMainIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

}
