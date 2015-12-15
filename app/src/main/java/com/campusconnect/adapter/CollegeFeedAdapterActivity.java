package com.campusconnect.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.campus_connect_2015.clubs.model.ModelsFeed;
import com.campusconnect.activity.InEventActivity;
import com.campusconnect.utility.CircularImageView;
import com.campusconnect.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by RK on 21-09-2015.
 */
public class CollegeFeedAdapterActivity extends
        RecyclerView.Adapter<CollegeFeedAdapterActivity.CollegeFeedViewHolder> {

    private List<ModelsFeed> CollegeFeedList;
    int posi=0;
    int going_click_count=0;
    int share_click_count=0;
    int flag_news=0;
    CharSequence EventTitles[]={"Roto Annual Play Auditions","Spark Session 4","E-Cell Startup Selection Drive","NITK Beach Clean-Up Drive","New Recruits List","Football Team Tryouts","Football Team Reaches Quarters of Independence Cup"};
    CharSequence GroupNames[]={"Rotaract Club","IE NITK","E-Cell","Rotaract Club","E-Cell","Football Team","Football Team"};
    CharSequence Timestamp[]={"1 day ago","2 days ago","Posted 10 days ago","11 days ago","Posted 15 days ago","Posted 20 days ago","Posted 30 days ago"};
    private static int[] event_photos = new int[] {
            R.mipmap.play_audition,
            R.mipmap.spark_session,
            R.mipmap.cell_event,
            R.mipmap.beach_clean_up,
            R.mipmap.cell_news,
            R.mipmap.football_event,
            R.mipmap.football_news
    };
    CharSequence Day[]={"MON","WED","MON","SAT","","MON",""};
    CharSequence Date_Month[]={"26 Oct","4 Oct","21 Sep","19 Sept","","14 Sep",""};
    CharSequence Time_[]={"5:30 PM","5:30 PM","5:30 PM","7:30 AM","","5:30 PM",""};
    private static int[] GroupLogo = new int[] {
            R.mipmap.roto_logo,
            R.mipmap.ie_logo,
            R.mipmap.cell_logo,
            R.mipmap.roto_logo,
            R.mipmap.cell_logo,
            R.mipmap.football_logo,
            R.mipmap.football_logo
    };
    CharSequence Venue[]={"Main Building","ATB Seminar Hall","MSH","NITK Beach Entrance","","Football Ground",""};
    CharSequence Description[]={"Be part of the greatest dramatics show of the college. If you love the stage, you will love to be a part of our show. Open to all.","SPARK is inspired by TEDx Talks where you will have a chance to interact, ask questions and learn from amazing people from your own campus!","Have an Idea? or want to Startup? Present your idea to us and you can win access to STEP resources and great mentorship.","All students are cordially invited for the NITK Beach Clean-Up Drive on September 19th (Saturday) as a part of the 'International Coastal Clean-Up Day' Celebration. This event is being held in association with NSS NITK & the Rotaract Club of our college.\n","The new recruits are the following:\n14EE201 Abhijay Kumar Pandit\n14ME139 Parikshit\n14EC202 Aditya Nishtala\n14CO132 Prajwal Kailas\n14CH02 Aishwarya Sanjeev Kumar\n14CV136 Pralay S","Be a part of the football team! Come and show us what you got. Talented and enthusiastic first years are welcome.",
            "The NITK Football team won 2 games to reach the quarter finals of the independence cup. They lost their third game by a small 2-1 margin which put them out of the tournament."};

    public CollegeFeedAdapterActivity(List<ModelsFeed> CollegeFeedList) {
        this.CollegeFeedList = CollegeFeedList;
    }

    @Override
    public int getItemCount() {
        return CollegeFeedList.size();
    }

    @Override
    public void onBindViewHolder(CollegeFeedViewHolder college_feedViewHolder, int i) {
        ModelsFeed cf = CollegeFeedList.get(i);
       college_feedViewHolder.event_title.setText(cf.getTitle());
       college_feedViewHolder.group_name.setText(cf.getClubId());
       college_feedViewHolder.timestamp.setText(cf.getStartTime());

        String encodedImage = cf.getPhoto();
        if(encodedImage != null && !encodedImage.isEmpty()) {
            if (encodedImage.equals("None")) {
                college_feedViewHolder.event_photo.setImageResource(R.mipmap.spark_session);
            } else {
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                //college_feedViewHolder.event_photo.setImageResource(event_photos[i]);
                college_feedViewHolder.event_photo.setImageBitmap(decodedByte);
            }
        }
        else
        {
            college_feedViewHolder.event_photo.setImageResource(R.mipmap.spark_session);
        }

        //news
        if(cf.getAttendees()==null||cf.getAttendees().size()==0)
        {
           college_feedViewHolder.day.setVisibility(View.GONE);
           college_feedViewHolder.date_month.setVisibility(View.GONE);
           college_feedViewHolder.time.setVisibility(View.GONE);
            college_feedViewHolder.news_icon.setVisibility(View.VISIBLE);
            flag_news=1;
            college_feedViewHolder.going.setImageResource(R.drawable.selector_heart);
        }

        else {
            SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = inFormat.parse(cf.getStartDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
            String goal = outFormat.format(date);
            Log.e("day", goal);
            Log.e("entry",cf.getStartDate());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
            String month = monthFormat.format(date);
            Log.e("month",month);
            college_feedViewHolder.day.setText(goal);
          // college_feedViewHolder.date_month.setText(Date_Month[i]);
           //college_feedViewHolder.time.setText(Time_[i]);
            college_feedViewHolder.news_icon.setVisibility(View.GONE);
            college_feedViewHolder.going.setImageResource(R.mipmap.going);
        }

       college_feedViewHolder.group_icon.setImageResource(R.mipmap.ie_logo);

    }

    @Override
    public CollegeFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.activity_card_layout_college_feed, viewGroup, false);

        return new CollegeFeedViewHolder(itemView);
    }

    public class CollegeFeedViewHolder extends RecyclerView.ViewHolder {

        CardView college_feed ;
        TextView event_title, group_name, timestamp, day,date_month,time;
        ImageView event_photo, news_icon,going,share;
        CircularImageView group_icon;
        public CollegeFeedViewHolder(View v) {
            super(v);

            college_feed = (CardView)v.findViewById(R.id.college_feed_card);
            event_title = (TextView) v.findViewById(R.id.tv_event);
            group_name = (TextView) v.findViewById(R.id.tv_group);
            timestamp = (TextView) v.findViewById(R.id.tv_timestamp);
            event_photo = (ImageView) v.findViewById(R.id.iv_event_photo);
            going = (ImageView) v.findViewById(R.id.iv_going);
            share = (ImageView) v.findViewById(R.id.iv_share);
            news_icon = (ImageView) v.findViewById(R.id.iv_news_icon);
            day = (TextView) v.findViewById(R.id.tv_day);
            date_month = (TextView) v.findViewById(R.id.tv_date_month);
            time = (TextView) v.findViewById(R.id.tv_time);
            group_icon = (CircularImageView) v.findViewById(R.id.group_image);

            college_feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent_temp = new Intent(v.getContext(), InEventActivity.class);
                    posi=getPosition();
                    //Create the bundle
                    Bundle bundle = new Bundle();
                    Log.e("Selected",CollegeFeedList.get(posi).toString());
                    bundle.putString("E_NAME", CollegeFeedList.get(posi).getTitle());
                    bundle.putString("E_TIME",CollegeFeedList.get(posi).getStartTime());
                    bundle.putString("E_DATE",CollegeFeedList.get(posi).getStartDate());
                    bundle.putString("G_NAME",CollegeFeedList.get(posi).getClubId());
                    bundle.putString("V_NAME",CollegeFeedList.get(posi).getVenue());
                    bundle.putString("E_DESCRIPTION",CollegeFeedList.get(posi).getDescription());
             //       bundle.putInt("E_PHOTO", event_photos[posi]);
               //     bundle.putInt("G_PHOTO",GroupLogo[posi]);
                    bundle.putInt("FLAG_NEWS_TOP",flag_news);
                    bundle.putInt("POSITION_CF",posi);
                    intent_temp.putExtras(bundle);
                    v.getContext().startActivity(intent_temp);
                }
            });

            going.setAlpha((float) 0.5);
            going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (going_click_count % 2 == 0) {
                        going.setAlpha((float) 1);
                    } else {
                        going.setAlpha((float) 0.5);
                    }
                    going_click_count++;
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i=new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    String shareBody = "Title : "+CollegeFeedList.get(posi).getTitle() + "/n" + "Description : "+CollegeFeedList.get(posi).getDescription()+" for more info visit http://campusconnect.cc/";

                    //String shareBody = CollegeFeedList.get(posi).getTitle()+"/n"+CollegeFeedList.get(posi).getDescription();
                    i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    v.getContext().startActivity(Intent.createChooser(i, "Share via"));
//
//                    Intent sharingIntent = new Intent();
//                    sharingIntent.setAction(Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
//                    v.getContext().startActivity(sharingIntent);
                    if(share_click_count%2==0) {
                        share.setAlpha((float) 1);
                    }
                    else {
                        share.setAlpha((float) 0.5);
                    }
                    share_click_count++;
                }
            });

        }

    }
}

