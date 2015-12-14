package com.campusconnect.activity;

/**
 * Created by Rishab on 09-10-2015.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.campus_connect_2015.clubs.Clubs;
import com.appspot.campus_connect_2015.clubs.model.ModelsProfileMiniForm;
import com.campusconnect.R;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.gcm.RegistrationIntentService;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class GoogleSignin extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "GoogleSignin";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess, temporary;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private LinearLayout llProfileLayout;

    private static final String LOG_TAG = "GoogleSignin";

    private String mEmailAccount = "";
    SharedPreferences sharedpreferences;

    File follows;
    File members;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_signin);

        sharedpreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);

        temporary = (Button) findViewById(R.id.temporary);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);

        members = new File(this.getFilesDir(), "Members.txt");
        follows = new File(this.getFilesDir(), "Follows.txt");
        // Button click listeners
        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);
        temporary.setOnClickListener(this);

        boolean gcmstatus = sharedpreferences.getBoolean("gcm_generated", false);
        if (gcmstatus == false) {
            Log.e(TAG, "here now");
            Intent m = new Intent(GoogleSignin.this, RegistrationIntentService.class);
            ComponentName a = startService(m);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(Plus.API, Plus.PlusOptions.builder().build()).
                addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        updateUI(true);

    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            temporary.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            temporary.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
                mEmailAccount = email;


                SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.EMAIL_KEY, email);
                SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.PERSON_NAME, personName);


              /*  SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(AppConstants.EMAIL_KEY, email);
                editor.putString(AppConstants.PERSON_NAME, personName);

                editor.commit();*/


                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);


            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }



    /**
     * Button on click listener
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                // Signin button clicked
                signInWithGplus();
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signOutFromGplus();
                break;
            case R.id.btn_revoke_access:
                // Revoke access button clicked
                revokeGplusAccess();
                break;
            case R.id.temporary:
                //TODO  network check
                if (NetworkAvailablity.hasInternetConnection(GoogleSignin.this)) {
                    //   checkExists(v);
                    //TODO removed old calling
                    webApiProfile();
                } else {
                    Toast.makeText(GoogleSignin.this, "Network is not available.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void webApiProfile() {
        if (!isSignedIn()) {
            Toast.makeText(this, "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject obj = new JSONObject();
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        String url = WebServiceDetails.DEFAULT_BASE_URL + "profile?email=" + mEmailAccount;
        Log.e(TAG, "get profile" + url);

        new WebRequestTask(GoogleSignin.this, param, _handler, WebRequestTask.GET, obj, WebServiceDetails.PID_GET_PROFILE,
                true, url).execute();
    }

    public void checkExists(final View v) {
        if (!isSignedIn()) {
            Toast.makeText(this, "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }


        AsyncTask<Void, Void, ModelsProfileMiniForm> getProfile =
                new AsyncTask<Void, Void, ModelsProfileMiniForm>() {

                    @Override
                    protected ModelsProfileMiniForm doInBackground(Void... params) {
                        if (!isSignedIn()) {
                            return null;
                        }
                        if (!AppConstants.checkGooglePlayServicesAvailable(GoogleSignin.this)) {
                            return null;
                        }
                        // Create a Google credential since this is an authenticated request to the API.
                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                                GoogleSignin.this, AppConstants.AUDIENCE);
                        credential.setSelectedAccountName(mEmailAccount);

                        // Retrieve service handle using credential since this is an authenticated call.
                        Clubs apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                        try {
                            Clubs.GetProfile gp = apiServiceHandle.getProfile(mEmailAccount);
                            ModelsProfileMiniForm profileList = gp.execute();
                            Log.e(LOG_TAG, "SUCCESS");
                            Log.e(LOG_TAG, profileList.toPrettyString());
                            return profileList;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }


                    @Override
                    protected void onPostExecute(ModelsProfileMiniForm mpList) {
                        if (mpList != null) {
                            //TODO changes the shared preferance style

                            SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putString(AppConstants.BATCH, mpList.getBatch());
                            edit.putString(AppConstants.BRANCH, mpList.getBranch());
                            if (mpList.getIsAlumni().equals("Y")) {
                                edit.putString(AppConstants.PROFILE_CATEGORY, AppConstants.ALUMNI);
                            } else {
                                edit.putString(AppConstants.PROFILE_CATEGORY, AppConstants.STUDENT);
                            }
                            edit.putString(AppConstants.PHONE, mpList.getPhone());
                            edit.putString(AppConstants.COLLEGE_ID, mpList.getCollegeId());
                            edit.putString(AppConstants.PERSON_PID, mpList.getPid());


                            //TODO query = Saving data in file why ?

                            BufferedWriter bfr = null;
                            FileOutputStream fos = null;
                            if (mpList.getFollows() != null) {
                                if (!follows.exists()) {
                                    try {
                                        follows.createNewFile();
                                        Log.e(LOG_TAG, Boolean.valueOf(follows.exists()).toString());

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    fos = new FileOutputStream(follows);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                bfr = new BufferedWriter(new OutputStreamWriter(fos));
                                StringBuilder sb = new StringBuilder();

                                for (int i = 0; i < mpList.getFollows().size(); i++) {
                                    sb.append(mpList.getFollows().get(i) + "|" + mpList.getFollowsNames().get(i) + "\n");
                                }

                                if (mpList.getClubNames() != null) {
                                    for (int i = 0; i < mpList.getClubNames().size(); i++) {
                                        sb.append(mpList.getClubNames().get(i).getClubId() + "|" + mpList.getClubNames().get(i).getName() + "\n");
                                    }
                                }
                                try {
                                    bfr.write(sb.toString());
                                    bfr.close();
                                    fos.close();
                                    Log.e("file", "written succes");
                                    //bfr=null;
                                    //sb.delete(0,sb.toString().length());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (mpList.getClubNames() != null) {
//                                if(!members.exists()){
//                                    try {
//                                        members.createNewFile();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                try {
//                                    fos=new FileOutputStream(members);
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                }
//                                bfr=new BufferedWriter(new OutputStreamWriter(fos));
//                                StringBuilder sb=new StringBuilder();
//
//                                for (int i = 0; i < mpList.getClubNames().size(); i++) {
//                                    sb.append(mpList.getClubNames().get(i).getClubId() + "|" + mpList.getClubNames().get(i).getName());
//                                }

//                                try {
//                                    bfr.write(sb.toString());
//                                    bfr.close();
//                                    fos.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                            }
                            edit.commit();
                            Intent intent_temp = new Intent(v.getContext(), MainActivity.class);
                            startActivity(intent_temp);
                            finish();

                        } else {
                            Intent intent_temp = new Intent(v.getContext(), SelectCollegeActivity.class);
                            startActivity(intent_temp);
                            finish();
                        }
                    }
                };
        getProfile.execute((Void) null);


    }


    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_GET_PROFILE: {
                            try {
                                Log.e("Profile Response", strResponse);
                                JSONObject jsonResponse = new JSONObject(strResponse);


                                if (jsonResponse.getString("success").equalsIgnoreCase("true")) {
                                    JSONObject JsonResultObj = jsonResponse.optJSONObject("result");

                                    if (strResponse != null) {

                                        String name = JsonResultObj.optString("name");
                                        String pid = JsonResultObj.optString("pid");
                                        String batch = JsonResultObj.optString("batch");
                                        String collegeId = JsonResultObj.optString("collegeId");
                                        String phone = JsonResultObj.optString("phone");
                                        String branch = JsonResultObj.optString("branch");
                                        String isAlumni = JsonResultObj.optString("isAlumni");
                                        String email = JsonResultObj.optString("email");
                                        String kind = JsonResultObj.optString("kind");
                                        String etag = JsonResultObj.optString("etag");


                                        //  String followsStr=null;
                                        //  String followsNames = null;
                                        JSONArray followArray = null;
                                        JSONArray followNamesArray = null;
                                        if (jsonResponse.has("follows")) {
                                            //  followsNames=  jsonResponse.getJSONArray("follows_names").toString();
                                            followArray = jsonResponse.getJSONArray("follows");
                                        }
                                        if (jsonResponse.has("follows_names")) {
                                            followNamesArray = jsonResponse.getJSONArray("follows_names");

                                            // followsStr = jsonResponse.getJSONArray("follows").toString();
                                        }

                                        SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.BATCH, batch);
                                        SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.BRANCH, branch);
                                        SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.PHONE, phone);
                                        SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.COLLEGE_ID, collegeId);
                                        SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.PERSON_PID, pid);
                                        //  SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.FOLLOW, followsStr);
                                        // SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.FOLLOW_NAMES, followsNames);

                                        if (isAlumni.equalsIgnoreCase("Y")) {
                                            SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.ALUMNI);
                                        } else {
                                            SharedpreferenceUtility.getInstance(GoogleSignin.this).putString(AppConstants.PROFILE_CATEGORY, AppConstants.STUDENT);
                                        }

                                        //writing into the files

                                        BufferedWriter bfr = null;
                                        FileOutputStream fos = null;
                                        if (followArray != null && followArray.length() > 0) {
                                            if (!follows.exists()) {
                                                try {
                                                    follows.createNewFile();
                                                    Log.e(LOG_TAG, Boolean.valueOf(follows.exists()).toString());

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            try {
                                                fos = new FileOutputStream(follows);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            bfr = new BufferedWriter(new OutputStreamWriter(fos));
                                            StringBuilder sb = new StringBuilder();

                                            for (int i = 0; i < followArray.length(); i++) {

                                                String followstr = followArray.get(i).toString();
                                                String followNamesStr = followNamesArray.get(i).toString();
                                                sb.append(followstr + "|" + followNamesStr + "\n");
                                            }

                                   /* for (int i = 0; i < mpList.getFollows().size(); i++) {
                                        sb.append(mpList.getFollows().get(i) + "|" + mpList.getFollowsNames().get(i) + "\n");
                                    }



                                    if (mpList.getClubNames() != null) {
                                        for (int i = 0; i < mpList.getClubNames().size(); i++) {
                                            sb.append(mpList.getClubNames().get(i).getClubId() + "|" + mpList.getClubNames().get(i).getName() + "\n");
                                        }
                                    }*/
                                            try {
                                                bfr.write(sb.toString());
                                                bfr.close();
                                                fos.close();
                                                Log.e("file", "written succes");
                                                //bfr=null;
                                                //sb.delete(0,sb.toString().length());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                    startActivity(new Intent(GoogleSignin.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(GoogleSignin.this, SelectCollegeActivity.class));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                        default:
                            break;
                    }
                } else {
                    Toast.makeText(GoogleSignin.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(GoogleSignin.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            updateUI(false);
        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();

                            updateUI(false);
                        }

                    });
        }
    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
