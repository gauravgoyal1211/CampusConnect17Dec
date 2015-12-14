package com.campusconnect.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.campusconnect.R;
import com.campusconnect.adapter.NewsPostsByGroupAdapterActivity;
import com.campusconnect.bean.CampusFeedBean;
import com.campusconnect.communicator.WebRequestTask;
import com.campusconnect.communicator.WebServiceDetails;
import com.campusconnect.supportClasses.NewsPostsByGroup_infoActivity;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RK on 06/11/2015.
 */
public class NewsPostsByGroupActivity extends ActionBarActivity {

    RecyclerView news_posts_by_group;
    LinearLayout close;
    Typeface r_med;
    TextView news_posts_text;
    ArrayList<CampusFeedBean> postNewsList = new ArrayList<CampusFeedBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_posts_by_group);

        String clubid = getIntent().getStringExtra("clubId");
        WebApiGetPostNews(clubid);


        r_med = Typeface.createFromAsset(getAssets(), "font/Roboto_Medium.ttf");

        close = (LinearLayout) findViewById(R.id.cross_button);
        news_posts_text = (TextView) findViewById(R.id.tv_news_posts_text);
        news_posts_text.setTypeface(r_med);
        news_posts_text.setText("News Posts");
        news_posts_by_group = (RecyclerView) findViewById(R.id.rv_news_posts_by_group);
        news_posts_by_group.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        news_posts_by_group.setLayoutManager(llm);
        news_posts_by_group.setItemAnimator(new DefaultItemAnimator());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private List<NewsPostsByGroup_infoActivity> createList_newsPostsByGroupAdapterActivity(int size) {
        List<NewsPostsByGroup_infoActivity> result = new ArrayList<NewsPostsByGroup_infoActivity>();
        for (int i = 1; i <= size; i++) {
            NewsPostsByGroup_infoActivity ci = new NewsPostsByGroup_infoActivity();
            result.add(ci);
        }

        return result;
    }

    public void WebApiGetPostNews(String clubId) {
        try {
            JSONObject jsonObject = new JSONObject();
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            String url = WebServiceDetails.DEFAULT_BASE_URL + "getPosts?clubId=" + clubId;
            new WebRequestTask(NewsPostsByGroupActivity.this, param, _handler, WebRequestTask.GET, jsonObject, WebServiceDetails.PID_GET_Events,
                    true, url).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
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
                        case WebServiceDetails.PID_GET_Events: {
                            try {
                                JSONObject jsonObject = new JSONObject(strResponse);
                                if (jsonObject.has("items")) {
                                    JSONArray array = jsonObject.getJSONArray("items");
                                    if (array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {

                                            JSONObject innerObj = array.getJSONObject(i);
                                            CampusFeedBean bean = new CampusFeedBean();

/*

                                            "likers":"[]",
                                                    "description":"hdhdhdhdhdhd",
                                                    "from_pid":"Key('Profile', 4834276138811392)",
                                                    "views":"0",
                                                    "photoUrl":"None",
                                                    "likes":"0",
                                                    "time":"11:58:34",
                                                    "date":"2015-12-10",
                                                    "title":"news eventifjfhfh",
                                                    "clubphotoUrl":
                                            "https://lh3.googleusercontent.com/qe9m1joR-j7LRwyWUPcGN1sFwJU6-xzpMACHULIphKrCr1sj9tLBIQxBfXz_6EZNOGywbXxi-WRJqbsSGOm0ad-9kQBhgplkrw",
                                                    "kind":"clubs#resourcesItem"
*/
                                            String description = innerObj.optString("description");
                                            String pid = innerObj.optString("from_pid");
                                            String photo = innerObj.optString("photoUrl");

                                            //TODO remvpe comment here

                                            String clubname = innerObj.optString("clubname");
                                            String likes = innerObj.optString("likes");
                                            String views = innerObj.optString("views");
                                            String title = innerObj.optString("title");
                                            String time = innerObj.optString("time");
                                            String date = innerObj.optString("date");
                                            String clubphotoUrl = innerObj.optString("clubphotoUrl");
                                            String kind = innerObj.optString("kind");



                                            ArrayList<String> likesList = new ArrayList<>();
                                            try {

                                                if (innerObj.has("likers")) {
                                                    JSONArray attArray = innerObj.getJSONArray("likers");
                                                    if (attArray.length() > 0) {
                                                        for (int j = 0; j < attArray.length(); j++) {
                                                            String likersstr = attArray.getString(j);
                                                            likesList.add(likersstr);
                                                        }
                                                    }
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }

                                      //TOdo venue is missing
                                      //Creator name
                                            bean.setDescription(description);
                                            bean.setPid(pid);
                                            bean.setViews(views);
                                            bean.setPhoto(photo);
                                            bean.setLikes(likes);
                                            bean.setViews(views);
                                            bean.setTitle(title);
                                            bean.setTime(time);
                                            bean.setDate(date);
                                            bean.setKind(kind);
                                            bean.setClubphoto("" + clubphotoUrl);
                                            bean.setLikers("" + likesList.toString());
                                            bean.setTimeStamp(date+" "+time);
                                            bean.setClubid(clubname);
                                          //venue not be set
                                            bean.setVenue(" ");


                                         /*   ArrayList<String> tagList = new ArrayList<>();
                                            try {

                                                if (innerObj.has("tags")) {

                                                    JSONArray tagArray = innerObj.getJSONArray("tags");
                                                    if (tagArray.length() > 0) {
                                                        for (int l = 0; l < tagArray.length(); l++) {
                                                            String tag = tagArray.getString(l);
                                                            tagList.add(tag);
                                                        }

                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }*/

                                            postNewsList.add(bean);
                                        }

                                        NewsPostsByGroupAdapterActivity newsPostsByGroupAdapterActivity = new NewsPostsByGroupAdapterActivity(
                                                postNewsList, NewsPostsByGroupActivity.this);
                                        news_posts_by_group.setAdapter(newsPostsByGroupAdapterActivity);
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
                    Toast.makeText(NewsPostsByGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(NewsPostsByGroupActivity.this, "SERVER_ERROR", Toast.LENGTH_LONG).show();
            }
        }
    };

}


