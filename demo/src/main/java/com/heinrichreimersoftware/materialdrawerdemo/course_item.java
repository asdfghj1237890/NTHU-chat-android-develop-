package com.heinrichreimersoftware.materialdrawerdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heinrichreimersoftware.materialdrawerdemo.pager.course_pager;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class course_item extends Fragment implements TabLayout.OnTabSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;

    private final String TAG = "course_item";
    private Document document_unbox;
    private CardView cardView;
    String coursename;

    public course_item(){
        super();
    }

    @SuppressLint("ValidFragment")
    public course_item(String title){
        super();
        this.coursename = title;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View asView = inflater.inflate(R.layout.assignment, container, false);
        tabLayout = (TabLayout) asView.findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("公告"));
        tabLayout.addTab(tabLayout.newTab().setText("教材"));
        tabLayout.addTab(tabLayout.newTab().setText("作業"));
        tabLayout.addTab(tabLayout.newTab().setText("討論區"));
        tabLayout.addTab(tabLayout.newTab().setText("小組"));
        viewPager = (ViewPager) asView.findViewById(R.id.pager);
        course_pager adapter = new course_pager(getFragmentManager(), tabLayout.getTabCount());
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);
        return asView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(coursename);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public void analyseHTML(Document document){
        if (document!=null){
            Elements elements = document.select("div#profile");
            String[] title = elements.get(0).text().split(" ");
            Log.d(TAG, title[1]);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
