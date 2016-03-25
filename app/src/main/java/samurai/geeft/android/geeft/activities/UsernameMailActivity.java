package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by joseph on 25/03/16.
 */
public class UsernameMailActivity extends AppCompatActivity {

    private EditText mUsername,mEmail;
    private Button mButtonDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.username_mail);

        //TODO: Just for testing REMOVE
        Log.i("USERNAMEMAIL", "Inside UsernameMailActivity after inflating the layout.");

        mUsername=(EditText) findViewById(R.id.username_edittext);
        mEmail=(EditText) findViewById(R.id.email_edittext);
        mButtonDone=(Button) findViewById(R.id.username_request_button);

        mButtonDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mUsername != null) {
                    String email = mEmail.getText().toString();
                    if (isValidEmail(email)) {
//                        Set mail and username

                        //TODO: Just for testing REMOVE
                        Log.i("USERNAMEMAIL", "Valid mail.");

                        //BaasUser.current().;
                        finish();
                    } else {
                        Toast.makeText(UsernameMailActivity.this, R.string.no_valid_mail_toast, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UsernameMailActivity.this, R.string.no_valid_username_toast, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

//    private void sendFeedback(){
//
//        double userRatingCommunication = mRatingCommunication.getRating();
//        double userRatingReliability = mRatingReliability.getRating();
//        double userRatingCourtesy = mRatingCourtesy.getRating();
//        double userRatingDescription;
//        String userRatingComment = mRatingComment.getText().toString();
//
//
//        //if(mCallingActivity.equals("AssignedActivity")){ // I'm Geefter,I can't set Geeft description
//        if(mIamGeefter){
//            userRatingDescription = 0;
//        }
//        else{
//            userRatingDescription = mRatingDescription.getRating();
//        }
//
//        double[] feedbackArray = {
//                userRatingCommunication,
//                userRatingReliability,
//                userRatingCourtesy,
//                userRatingDescription
//        };
//
//        //TODO: ASyncTask
//        new BaaSUpdateUserFeedback(getApplicationContext(),mGeeft.getId()
//                ,mUsername,feedbackArray,userRatingComment,mIamGeefter,this).execute();
//        mProgressDialog = ProgressDialog.show(FeedbackPageActivity.this, "Attendere"
//                , "Salvataggio del feedback in corso");
//    }

}
