package com.campusconnect.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.GroupPageAdapterActivity;
import com.campusconnect.adapter.GroupPage_infoActivity;
import com.campusconnect.bean.GroupBean;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.database.DatabaseHandler;
import com.campusconnect.utility.NetworkAvailablity;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupPageActivity extends ActionBarActivity {
    RecyclerView group_page;
    String follow;
    DatabaseHandler db;
    private static final String LOG_TAG = "GroupPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);
        db = new DatabaseHandler(GroupPageActivity.this);
        group_page = (RecyclerView) findViewById(R.id.recycler_group_page);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        group_page.setLayoutManager(llm);
        group_page.setHasFixedSize(true);
        group_page.setItemAnimator(new DefaultItemAnimator());
        GroupBean bean = (GroupBean) getIntent().getSerializableExtra("BEAN");
        try {
            Bundle bundle = getIntent().getExtras();
            follow = bundle.getString("follow");
            if (follow == null) {
                follow = "0";
            }
        } catch (Exception e) {
        }
        if (bean != null) {
            String club_id = bean.getClubId();
            WebApiGetGroup(club_id);
        }
    }
    private List<GroupPage_infoActivity> createList_group_page(int size) {

        List<GroupPage_infoActivity> result = new ArrayList<GroupPage_infoActivity>();
        for (int i = 1; i <= size; i++) {
            GroupPage_infoActivity ci = new GroupPage_infoActivity();
            result.add(ci);
       }
        return result;
    }

    public void WebApiGetGroup(String club_id) {
        if (NetworkAvailablity.hasInternetConnection(GroupPageActivity.this)) {
            try {
                String pid= SharedpreferenceUtility.getInstance(GroupPageActivity.this).getString(AppConstants.PERSON_PID);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("club_id", "" + club_id);
                jsonObject.put("pid",pid);
                //     jsonObject.put("pid", "" + pid);
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                String url = WebServiceDetails.DEFAULT_BASE_URL + "getClub";
                Log.e("getGroup", jsonObject.toString());
                Log.e("", url);

                new WebRequestTask(GroupPageActivity.this, param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_GET_CLUB,
                        true, url).execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(GroupPageActivity.this, "Network is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;

            if (response_code != 0 && response_code != 204) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {

                    switch (response_code) {
                        case WebServiceDetails.PID_GET_CLUB: {
                            try {
                                JSONObject grpJson = new JSONObject(strResponse);
                                String kind = grpJson.optString("kind");
                                String etag = grpJson.optString("etag");
                                String description = grpJson.optString("description");
                                String admin = grpJson.optString("admin");
                                String clubId = grpJson.optString("club_id");
                                String abb = grpJson.optString("abbreviation");
                                String name = grpJson.optString("name");
                                String photoUrl = grpJson.optString("photoUrl");
                                String followcount = grpJson.optString("followercount");
                                String member_count = grpJson.optString("membercount");
                                String isMember = grpJson.optString("isMember");
                                String isFollower = grpJson.optString("isFollower");

                                GroupBean bean = new GroupBean();
                                bean.setAbb(abb);
                                bean.setName(name);
                                bean.setAdmin(admin);
                                bean.setClubId(clubId);
                                if (isFollower.equalsIgnoreCase("Y")) {
                                    bean.setFollow("1");
                                    int i = db.updateFollow(clubId, "1");

                                } else {
                                    bean.setFollow("0");
                                    int i = db.updateFollow(clubId, "0");
                                }
                                if (isMember.equalsIgnoreCase("Y")) {
                                    bean.setIsMember("1");
                                } else {
                                    bean.setIsMember("0");
                                }
                                bean.setDescription(description);
                                bean.setPhotourl(photoUrl);
                                bean.setMemberCount(member_count);
                                bean.setFollowCount(followcount);
                                GroupPageAdapterActivity gp = new GroupPageAdapterActivity(createList_group_page(5), bean, GroupPageActivity.this);
                                group_page.setAdapter(gp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }
    };
}
