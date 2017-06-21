package com.ilmsplus.demo;
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

import com.ilmsplus.demo.pager.Pager;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class assignment extends Fragment implements TabLayout.OnTabSelectedListener{
    TabLayout tabLayout;
    ViewPager viewPager;

    private final String TAG = "assignment";
    private Document document_unbox;
    private CardView cardView;

    String title;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View asView = inflater.inflate(R.layout.assignment, container, false);
        tabLayout = (TabLayout) asView.findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("未完成"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        viewPager = (ViewPager) asView.findViewById(R.id.pager);
        Pager adapter = new Pager(getFragmentManager(), tabLayout.getTabCount());
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
        getActivity().setTitle("作業");
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