package com.campusconnect.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.campusconnect.R;
import com.campusconnect.adapter.NotificationAdapterActivity;
import com.campusconnect.bean.NotificationBean;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.constant.AppConstants;
import com.campusconnect.utility.SharedpreferenceUtility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    RecyclerView notification_list;
    ImageButton noti, profile, home, calendar, search;
    View mRootView;
    NotificationAdapterActivity nl;
    ArrayList<NotificationBean> mNotifiList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.activity_notification, container, false);


            notification_list = (RecyclerView) mRootView.findViewById(R.id.rv_notification);
            notification_list.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            notification_list.setLayoutManager(llm);
            notification_list.setItemAnimator(new DefaultItemAnimator());

            WebApiNotification();
           /* nl = new NotificationAdapterActivity(
                    createList_nl(4));*/
            //   notification_list.setAdapter(nl);


        } catch (InflateException e) {
            e.printStackTrace();
        }
        return mRootView;
    }

    public void WebApiNotification() {
        try {
            String pid = SharedpreferenceUtility.getInstance(getActivity()).getString(AppConstants.PERSON_PID);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("pid", pid);
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            String url = WebServiceDetails.DEFAULT_BASE_URL + "myNotifications";
            new WebRequestTask(getActivity(), param, _handler, WebRequestTask.POST, jsonObject, WebServiceDetails.PID_NOTIFICATION,
                    true, url).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


  /*  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        overridePendingTransition(R.anim.pushfront, R.anim.centertoright);*/

       /* home = (ImageButton) findViewById(R.id.ib_home);
        noti = (ImageButton) findViewById(R.id.ib_notification);
        profile = (ImageButton) findViewById(R.id.ib_profile);
        calendar = (ImageButton) findViewById(R.id.ib_calendar);
        search = (ImageButton) findViewById(R.id.ib_search);*/


    //     home.setOnClickListener(this);
    //   calendar.setOnClickListener(this);
    //  profile.setOnClickListener(this);
    //  search.setOnClickListener(this);

    //   }

   /* //TODO  changes in the listner
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_profile:
                Intent intent_profile = new Intent(v.getContext(), ProfilePageFragment.class);
                startActivity(intent_profile);
                finish();
                break;
            case R.id.ib_home:
                Intent intent_home = new Intent(v.getContext(), HomeFragment.class);
                startActivity(intent_home);
                overridePendingTransition(R.anim.pushfront, R.anim.centertoright);
                finish();
                break;
            case R.id.ib_calendar:
                Intent intent_noti = new Intent(v.getContext(), CalenderFragment.class);
                startActivity(intent_noti);
                finish();
                break;
            case R.id.ib_search:
                Intent intent_cal = new Intent(v.getContext(), SearchFragment.class);
                startActivity(intent_cal);
                finish();
                break;
        }
    }*/


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    private List<NotificationBean> createList_nl(int size) {
        List<NotificationBean> result = new ArrayList<NotificationBean>();
        for (int i = 1; i <= size; i++) {
            NotificationBean ci = new NotificationBean();
            result.add(ci);
        }
        return result;
    }


    private final Handler _handler = new Handler() {
        public void handleMessage(Message msg) {
            int response_code = msg.what;
            if (response_code != 0) {
                String strResponse = (String) msg.obj;
                Log.v("Response", strResponse);
                if (strResponse != null && strResponse.length() > 0) {
                    switch (response_code) {
                        case WebServiceDetails.PID_NOTIFICATION: {
                            try {
                                mNotifiList.clear();
                                JSONObject jsonObject = new JSONObject(strResponse);
                                if (jsonObject.has("list")) {

                                    JSONArray array = jsonObject.getJSONArray("list");
                                    if (array.length() > 0) {

                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);
                                            String gropname = obj.optString("clubName");
                                            String type = obj.optString("type");
                                            String groupId = obj.optString("clubId");
                                            String timestamp = obj.optString("timestamp");
                                            String eventName = obj.optString("eventName");
                                            String eventid = obj.optString("eventId");
                                            String photoUrl = obj.optString("clubphotoUrl");
                                            String postName= obj.optString("postName");

                                            NotificationBean bean = new NotificationBean();
                                            bean.setGroup_name(gropname);
                                            bean.setGroupId(groupId);
                                            bean.setType(type);
                                            bean.setEventName(eventName);
                                            bean.setTimestamp(timestamp);
                                            bean.setPhotoUrl(photoUrl);
                                            bean.setPostName(postName);

                                            mNotifiList.add(bean);
                                        }
                                        nl = new NotificationAdapterActivity(mNotifiList, getActivity());
                                        notification_list.setAdapter(nl);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "SERVER_ERROR", Toast.LENGTH_LONG).show();
            }
        }
    };


}
