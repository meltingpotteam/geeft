package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 07/03/16.
 */
public class BaaSUpdateUserFeedback extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private String mFeedbackComment;
    private Context mContext;
    private String mHisBaasboxUsername;
    private boolean mIsGeefter;
    private double[] mFeedbackArray;
    private TaskCallbackBooleanToken mCallback;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------
    private BaasUser mBaasUser;
    private String mGeeftId;

    /**
     *
     **/
    public BaaSUpdateUserFeedback(Context context, String geeftId ,String hisBaasboxUsername, double[] feedbackArray,String feedbackComment,
                                  boolean isGeefter,TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftId = geeftId;
        mHisBaasboxUsername = hisBaasboxUsername;
        mFeedbackArray = feedbackArray;
        mFeedbackComment = feedbackComment;
        mIsGeefter = isGeefter;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        boolean result;
        if(BaasUser.current() !=null) {
            Log.d(TAG,"mHisBaasboxUsername:" + mHisBaasboxUsername);
            BaasResult<BaasUser> resUser = BaasUser.fetchSync(mHisBaasboxUsername);
            if(resUser.isSuccess()){
                BaasUser user = resUser.value();
                double currentFeedback = user.getScope(BaasUser.Scope.REGISTERED).getDouble("feedback");
                float n_feedback = user.getScope(BaasUser.Scope.REGISTERED).getFloat("n_feedback");

                double newUserFeedback = calculateFeedback(currentFeedback, n_feedback);

                BaasDocument userFeedbackDocument = new BaasDocument(TagsValue.COLLECTION_USERS_FEEDBACKS);
                createUserFeedbackDocument(userFeedbackDocument);
                if(saveUserFeedbackDocument(userFeedbackDocument)){
                    if(getBaasUser(mHisBaasboxUsername)) {
                        if (createLinkBetweenUserAndFeedback(userFeedbackDocument)) {
                           if(putNewUserFeedbackInBaasbox(newUserFeedback,n_feedback)){
                               if(setFeedbackLeft()){
                                   return true;
                               }
                           }
                        }
                    }
                }
            }
            else{
                if(resUser.error() instanceof BaasInvalidSessionException){
                    Log.e(TAG,"Invalid Session Token");
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                }
                else{
                    Log.e(TAG,"Error while fetching user");
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else{
            mResultToken = RESULT_SESSION_EXPIRED;
            return false;
        }
        return false;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result, mResultToken);
    }

    private double calculateFeedback(double oldUserFeedback,float n_feedback){
        double userRatingCommunication = mFeedbackArray[0];
        double userRatingReliability =  mFeedbackArray[1];
        double userRatingCourtesy =  mFeedbackArray[2];
        double userRatingDescription =  mFeedbackArray[3];

        double newFeedback;

        //-- Geefted Feedback calculation. Communication 30% Reliability 40% Courtesy 30%
        //if(mCallingActivity.equals("AssignedActivity")) {
        if(!mIsGeefter){
            newFeedback = userRatingCommunication * 0.3 + userRatingReliability * 0.4 + userRatingCourtesy * 0.3;
        }
        //-- Geefter Feedback calculation. Communication 20% Reliability 30% Description 30% Courtesy 20%
        else {
            newFeedback = userRatingCommunication * 0.2 + userRatingDescription * 0.3 + userRatingReliability * 0.3 + userRatingCourtesy * 0.2;
        }

        double newUserFeedback = (oldUserFeedback + newFeedback) / (n_feedback+1);

        return newUserFeedback;
    }

    private void createUserFeedbackDocument(BaasDocument userFeedbackDocument) {
        userFeedbackDocument.put("commication",mFeedbackArray[0]);
        userFeedbackDocument.put("reliability",mFeedbackArray[1]);
        userFeedbackDocument.put("courtesy",mFeedbackArray[2]);
        userFeedbackDocument.put("description",mFeedbackArray[3]);
        userFeedbackDocument.put("comment",mFeedbackComment);
        userFeedbackDocument.put("givenBy", mHisBaasboxUsername);

    }

    private boolean saveUserFeedbackDocument(BaasDocument userFeedbackDocument) {
        BaasResult<BaasDocument> resSaveDoc = userFeedbackDocument.saveSync();
        if(resSaveDoc.isSuccess()){
            return true;
        }
        else{
            if(resSaveDoc.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while saving userFeedbackDocument");
                mResultToken = RESULT_FAILED;
                return false;
            }
        }

    }

    private boolean getBaasUser(String hisBaasboxUsername) {
        BaasResult<BaasUser> resUser = BaasUser.fetchSync(mHisBaasboxUsername);
        if(resUser.isSuccess()){
            mBaasUser = resUser.value();
            return true;
        }else{
            if(resUser.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while fetching user");
                mResultToken = RESULT_FAILED;
                return false;
            }
        }

    }

    private boolean createLinkBetweenUserAndFeedback(BaasDocument userFeedbackDocument) {
        String docUserId = mBaasUser.getScope(BaasUser.Scope.REGISTERED).get("doc_id");
        Log.d(TAG,"docId user: " + docUserId);
        BaasResult<BaasLink> resLink = BaasLink.createSync(TagsValue.LINK_NAME_FEEDBACK, userFeedbackDocument.getId(), docUserId);
        if(resLink.isSuccess()){
            return true;
        }
        else{
            if(resLink.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while creating feedback link");
                Log.e(TAG,resLink.error().toString());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

    private boolean putNewUserFeedbackInBaasbox(double newUserFeedback,float n_feedback) {
        mBaasUser.getScope(BaasUser.Scope.REGISTERED).put("feedback",newUserFeedback);
        mBaasUser.getScope(BaasUser.Scope.REGISTERED).put("n_feedback",n_feedback + 1);
        BaasResult<BaasUser> resSaveScope = mBaasUser.saveSync();
        if(resSaveScope.isSuccess()){
            return true;
        }
        else{
            if(resSaveScope.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while fetching user");
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

    private boolean setFeedbackLeft(){
        BaasResult<BaasDocument> resGeeft = BaasDocument.fetchSync("geeft", mGeeftId);
        if(resGeeft.isSuccess()){
            BaasDocument geeft = resGeeft.value();
            if(mIsGeefter){
                geeft.put(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTER,true);
            }
            else{
                geeft.put(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTED,true);
            }
            BaasResult<BaasDocument> resUpdateGeeft = geeft.saveSync();
            if(resUpdateGeeft.isSuccess()){
                return true;
            }
            else{
                if(resUpdateGeeft.error() instanceof BaasInvalidSessionException){
                    Log.e(TAG,"Invalid Session Token");
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                }
                else{
                    Log.e(TAG,"Error while updating geeft document");
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else{
            if(resGeeft.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while fetching geeft document");
                mResultToken = RESULT_FAILED;
                return false;
            }
        }

    }

}


