package com.campusconnect.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campusconnect.R;
import com.campusconnect.bean.NotificationBean;
import com.campusconnect.slidingtab.SlidingTabLayout_Calendar;
import com.campusconnect.utility.StickyHeaderRecyclerDecor;
import com.campusconnect.utility.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

public class CalenderFragment extends Fragment {

    ViewPager pager;
    SlidingTabLayout_Calendar tabs;
    CharSequence Titles[] = {"Mon 5", "Tue 6", "Wed 7", "Thu 8", "Fri 9", "Sat 10", "Sun 11"};
    int Numboftabs = 7;

    RecyclerView calendar;
    View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        }
        try {
            mRootView = inflater.inflate(R.layout.activity_calendar_design_two, container, false);

            calendar = (RecyclerView) mRootView.findViewById(R.id.rv_calendar);

            // Set adapter populated with example dummy data
            CalendarAdapter adapter = new CalendarAdapter(
                    createList_cal(7));
            calendar.setAdapter(adapter);


            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            calendar.setLayoutManager(llm);

            // Add the sticky headers decoration
            final StickyHeaderRecyclerDecor headersDecor = new StickyHeaderRecyclerDecor(adapter);
            calendar.addItemDecoration(headersDecor);


            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    headersDecor.invalidateHeaders();
                }
            });
        } catch (InflateException e) {
            e.printStackTrace();
        }
        return mRootView;
    }


    private List<NotificationBean> createList_cal(int size) {
        List<NotificationBean> result = new ArrayList<NotificationBean>();
        for (int i = 1; i <= size; i++) {
            NotificationBean ci = new NotificationBean();
            result.add(ci);
        }

        return result;
    }

    public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {


        //Get the day from the server for each card and feed it to the getHeaderId(int position) function below.
        int dates[] = {1, 18, 18, 27, 27, 29, 29};

        public CalendarAdapter(List<NotificationBean> list_cal) {
        }

        @Override
        public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_card_layout_college_feed, parent, false);
            return new CalendarViewHolder(view) {
            };
        }
        @Override
        public void onBindViewHolder(CalendarViewHolder holder, int position) {

        }
        @Override
        public int getItemCount() {
            return 7;
        }

        @Override
        public long getHeaderId(int position) {
            char ch;
            ch = (char) (dates[position] + 64);
            return ch;
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_header, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(dates[position] + "");
            Typeface r_med = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/Roboto_Light.ttf");
            textView.setTypeface(r_med);
            holder.itemView.setBackgroundColor(Color.rgb(56, 56, 56));
        }

        public class CalendarViewHolder extends RecyclerView.ViewHolder {

            public CalendarViewHolder(View v) {
                super(v);
            }
        }


    }

}
