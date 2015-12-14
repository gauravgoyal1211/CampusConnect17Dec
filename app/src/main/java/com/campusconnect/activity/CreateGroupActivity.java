package com.campusconnect.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.campus_connect_2015.clubs.model.ModelsClubRequestMiniForm;
import com.campusconnect.R;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;
import com.google.common.base.Strings;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RK on 23-09-2015.
 */
public class CreateGroupActivity extends AppCompatActivity {

    static String imageUrlForUpload = "";
    ImageView upload;
    Button createGroup;
    EditText groupAbbreviation, groupName, groupDescription, groupType;
    LinearLayout close;
    ModelsClubRequestMiniForm cbr;
    private int PICK_IMAGE_REQUEST = 1;
    static SharedPreferences sharedPreferences;
    private String mEmailAccount = "";
    private static final String LOG_TAG = "CreateGroupActivity";
    TextView create_group_text;

    private final int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;

    String encodedImageStr;

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Typeface r_reg = Typeface.createFromAsset(getAssets(), "font/Roboto_Regular.ttf");
        Typeface r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");

        close = (LinearLayout) findViewById(R.id.cross_button);

        create_group_text = (TextView) findViewById(R.id.tv_create_group);
        createGroup = (Button) findViewById(R.id.et_createGroup);
        groupType = (EditText) findViewById(R.id.et_group_type);
        groupName = (EditText) findViewById(R.id.et_group_name);
        groupAbbreviation = (EditText) findViewById(R.id.et_group_abbreviation);
        groupDescription = (EditText) findViewById(R.id.et_group_description);
        upload = (ImageView) findViewById(R.id.group_icon_group);

        create_group_text.setTypeface(r_med);
        createGroup.setTypeface(r_reg);
        groupType.setTypeface(r_reg);
        groupName.setTypeface(r_reg);
        groupAbbreviation.setTypeface(r_reg);
        groupDescription.setTypeface(r_reg);


        sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
        mEmailAccount = sharedPreferences.getString(AppConstants.EMAIL_KEY, null);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webApiCreateGroup();

             /*   SharedPreferences sharedPreferences = v.getContext().getSharedPreferences(AppConstants.SHARED_PREFS, Context.MODE_PRIVATE);
                cbr = new ModelsClubRequestMiniForm();
                cbr.setClubName(groupName.getText().toString());
                cbr.setDescription(groupDescription.getText().toString());
                cbr.setAbbreviation(groupAbbreviation.getText().toString());
                cbr.setFromPid(sharedPreferences.getString(AppConstants.PERSON_PID, null));
                cbr.setCollegeId(sharedPreferences.getString(AppConstants.COLLEGE_ID, null));
                cbr.setPicture(encodedImageStr);
                Log.e("TYPE", groupName.getText().toString());
                createGroup(cbr);*/
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Start Activity To Select Image From Gallery
              /*  Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);*/
                //break;
                uploadImage();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
    }

//TODO getting error  from the response
    /*Getting server error no respones to show*/
  /*  "abbreviation" -> "tcgcgcgcgc"
            "club_name" -> "dttctctctf"
            "college_id" -> "6231550869897216"
            "from_pid" -> "5702666986455040"
            "description" -> "gcfcfcfcgcgcgv"*/


    public void webApiCreateGroup() {
        try {
            JSONObject jsonObject = new JSONObject();
            String collegeId = SharedpreferenceUtility.getInstance(CreateGroupActivity.this).getString(AppConstants.COLLEGE_ID);
            String pid = SharedpreferenceUtility.getInstance(CreateGroupActivity.this).getString(AppConstants.PERSON_PID);
            //   club_name,club_description,abbreviation,from_pid(profile ID),college_id
            String groupType = groupName.getText().toString();
            String groupname = groupName.getText().toString();
            String grpDes = groupDescription.getText().toString();
            String abbre = groupAbbreviation.getText().toString();

            if (groupType.isEmpty()||groupname.isEmpty() || grpDes.isEmpty() || abbre.isEmpty() || imageUrlForUpload.isEmpty()) {

                Toast.makeText(CreateGroupActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                return;
            }

            jsonObject.put("club_name", "" + groupName.getText().toString());
            jsonObject.put("description", "" + groupDescription.getText().toString());
            jsonObject.put("abbreviation", "" + groupAbbreviation.getText().toString());
            jsonObject.put("from_pid", pid);
            jsonObject.put("college_id", "" + collegeId);
            jsonObject.put("photoUrl", "" + imageUrlForUpload);
            Log.e("Json String", jsonObject.toString());
/*

            {
                "abbreviation": "test",
                    "club_name": "TEST1",
                    "college_id": "5644309118320640",
                    "from_pid": "5688424874901504",
                    "description": "test description"
            }

*/


            List<NameValuePair> param = new ArrayList<NameValuePair>();
            String url = WebServiceDetails.DEFAULT_BASE_URL + "club";
            new WebRequestTask(CreateGroupActivity.this, param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_CREATE_GROUP,
                    true, url).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void showAlertDialog(final File imageFile) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateGroupActivity.this);
        alertDialog.setTitle("Select image");
        alertDialog.setMessage("Do you want to upload this image?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                new Blob(CreateGroupActivity.this, imageFile).execute();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                upload.setImageResource(R.mipmap.upload);

            }
        });
        alertDialog.show();
    }

    void uploadImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"), 1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        switch (requestCode) {
            case 0:
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    //  _addPhotoBitmap = bitmap;
                    upload.setImageBitmap(bitmap);
                    upload.setScaleType(ImageView.ScaleType.FIT_XY);

                   /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] _byteArray = baos.toByteArray();
                    encodedImageStr = Base64.encodeToString(_byteArray, Base64.DEFAULT);*/


                    Uri selectedImageUri = getImageUri(CreateGroupActivity.this, bitmap);
                    File finalFile = new File(getPath(selectedImageUri, CreateGroupActivity.this));
                    if (finalFile.exists()) {
                        showAlertDialog(finalFile);
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    Uri selectedImageUri = data.getData();
                    String tempPath = getPath(selectedImageUri, CreateGroupActivity.this);
                    File finalFile = new File(getPath(selectedImageUri, CreateGroupActivity.this));
                    if (finalFile.exists()) {
                        showAlertDialog(finalFile);
                    }
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                    upload.setImageBitmap(bm);
                    upload.setScaleType(ImageView.ScaleType.FIT_XY);
                    encodedImageStr = Base64.encodeToString(getBytesFromBitmap(bm), Base64.NO_WRAP);

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    Toast.makeText(CreateGroupActivity.this, "Image size is too large.Please upload small image.", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }


    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                if (response_code != 204) {
                    String strResponse = (String) msg.obj;
                    Log.v("Response", strResponse);
                    if (strResponse != null && strResponse.length() > 0) {
                        switch (response_code) {
                            case WebServiceDetails.PID_CREATE_GROUP: {
                                try {
                                    JSONObject jsonResponse = new JSONObject(strResponse);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                            default:
                                break;
                        }
                    } else {
                        Toast.makeText(CreateGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Your group request has been sent for approval.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {

                Toast.makeText(CreateGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();

            }
        }
    };

    public class Blob extends AsyncTask<Void, Integer, String> {
        String url;
        File imageFIle;

        public Blob(String Url) {
            this.url = Url;
        }

        private ProgressDialog dialog;
        private Context context;
        private Handler handler;
        public static final int POST = 1;
        private boolean showDialog;
        String responseStringfianl;

        private int type;


        public Blob(Context context, File imageFIle) {
            this.context = context;
            this.url = url;
            this.imageFIle = imageFIle;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Please wait...");
                dialog.show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (dialog != null)
                    dialog.dismiss();
                super.onPostExecute(result);

                imageUrlForUpload = result;

                //   Toast.makeText(context, "" + result, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://campus-connect-2015.appspot.com/get_upload_url");
                httpget.setHeader("Content-Type", "application/json");
                httpget.setHeader("Accept-Encoding", "gzip");
                //  httpget.setHeader("");
                //  entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");//text/plain;charset=UTF-8
                HttpResponse response = httpClient.execute(httpget);
                int responsecode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(response.getEntity());
                Log.v("", "responsessdf : " + responseString);
                if (responseString != null) {
                    SharedpreferenceUtility.getInstance(context).putString(AppConstants.BLOB_URL, responseString);
                }


                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(responseString);
                String BOUNDARY = "Boundary-8B33EF29-2436-47F6-A415-62EF61F62D14";

                FileBody fileBody = new FileBody(imageFIle);
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, BOUNDARY, Charset.defaultCharset());
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                HttpResponse response1 = httpclient.execute(httppost);
                responseStringfianl = EntityUtils.toString(response1.getEntity());
                Log.e("response String", responseStringfianl.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            return responseStringfianl;
        }


    }
}
