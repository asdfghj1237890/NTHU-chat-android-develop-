package com.heinrichreimersoftware.materialdrawerdemo.hw_am;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;


import com.heinrichreimersoftware.materialdrawerdemo.MyAdapter;
import com.heinrichreimersoftware.materialdrawerdemo.RoundedRectProgressBar;
import com.heinrichreimersoftware.materialdrawerdemo.done_Adapter;
import com.heinrichreimersoftware.materialdrawerdemo.MyDBHelper;
import com.heinrichreimersoftware.materialdrawerdemo.R;
import com.heinrichreimersoftware.materialdrawerdemo.item.Item;
import com.heinrichreimersoftware.materialdrawerdemo.item.done_progress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class doneam extends Fragment {
    private RecyclerView mList;
    private MyDBHelper databaseHelper;
    private ArrayList<done_progress> arrayList_new = new ArrayList<done_progress>();
    private ArrayList<String> course_list = new ArrayList<String>();
    private ArrayList<String> hw_list = new ArrayList<String>();
    private Cursor cursor;
    private done_Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ArrayList<Integer> completing_array = new ArrayList<Integer>();
    public ArrayList<Integer> counting_array = new ArrayList<Integer>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        final View doneView = inflater.inflate(R.layout.doneam, container, false);

        RecyclerView mList = (RecyclerView) doneView.findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) doneView.findViewById(R.id.refresh);
        loadDatabase(doneView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);
        //MyAdapter myAdapter = new MyAdapter(myHeadset,myDateset,mList);

        adapter = new done_Adapter(getActivity(),arrayList_new,mList,completing_array,counting_array);
        adapter.notifyDataSetChanged();
        //設置刷新監聽
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDatabase(doneView);
            }
        });
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        return doneView;
    }

    public void loadDatabase(View v)
    {
        arrayList_new.clear();
        hw_list.clear();
        course_list.clear();
        completing_array.clear();
        counting_array.clear();
        SimpleDateFormat system_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RecyclerView mList = (RecyclerView) v.findViewById(R.id.list_view);
        databaseHelper = new MyDBHelper(getActivity());
        try {
            databaseHelper.checkAndCopyDatabase();
            databaseHelper.openDatabase();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        try{
            cursor = databaseHelper.QueryData("select * from main.exp");
            if(cursor != null){
                /*if(cursor.moveToFirst()){
                    do {
                        done_progress done = new done_progress();
                        done.setCourse_head(cursor.getString(1));
                        arrayList_new.add(done);
                    }while (cursor.moveToNext());
                }*/
                Date now = new Date(System.currentTimeMillis());
                if(cursor.moveToFirst()) {
                    String course_title = cursor.getString(1);
                    String Date_hw = cursor.getString(5);
                    String hw_name = cursor.getString(3);
                    if(!course_list.contains(course_title)) {
                        try {
                            Date parsed_date = system_date.parse(Date_hw);
                            course_list.add(course_title);
                            hw_list.add("⏏"+hw_name);
                            completing_array.add(1);
                            if (parsed_date.compareTo(now) == 1)
                                counting_array.add(1);
                            else
                                counting_array.add(0);
                            Log.d("not contain", "first element");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    while (cursor.isLast() != true) {
                        if (!course_list.contains(cursor.getString(1))) {
                            Date_hw = cursor.getString(5);
                            hw_name = cursor.getString(3);
                            course_list.add(cursor.getString(1));
                            hw_list.add("⏏"+cursor.getString(3));
                            int qq0 = course_list.indexOf(cursor.getString(1));
                            Log.d("index", qq0+"");
                            try{
                                completing_array.set(qq0,1);
                            }catch (Exception e){
                                completing_array.add(1);
                            }
                            try {
                                Date parsed_date = system_date.parse(Date_hw);
                                if (parsed_date.compareTo(now) == 1) {
                                    counting_array.set(course_list.indexOf(cursor.getString(1)), 1);
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            cursor.moveToNext();
                        }
                        else{
                            Date_hw = cursor.getString(5);
                            hw_name = "⏏"+cursor.getString(3);
                            int qq1 = course_list.indexOf(cursor.getString(1));
                            Log.d("index", qq1+"");
                            Log.d("temp", completing_array.size()+"");
                            Log.d("temp1", counting_array.size()+"");
                            int temp = completing_array.get(qq1);
                            String hw_temp = hw_list.get(qq1);
                            int temp1;
                            try {
                                temp1 = counting_array.get(qq1);
                            }catch (Exception e){
                                temp1 = 0;
                                counting_array.add(0);
                            }
                            completing_array.set(course_list.indexOf(cursor.getString(1)),temp+1);
                            hw_list.set(course_list.indexOf(cursor.getString(1)),hw_temp+"\n"+hw_name);
                            try {
                                Date parsed_date = system_date.parse(Date_hw);
                                if (parsed_date.compareTo(now) == 1)
                                    counting_array.set(course_list.indexOf(cursor.getString(1)),temp1+1);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            cursor.moveToNext();
                        }
                    }
                    for(int i=0; i < course_list.size(); i++){
                        done_progress done = new done_progress();
                        done.setCourse_head(course_list.get(i).toString());
                        done.setDescription(hw_list.get(i).toString());
                        //Toast.makeText(getActivity(),hw_list.get(i).toString(), Toast.LENGTH_SHORT).show();
                        arrayList_new.add(done);
                    }
                }
                //adapter.refreshItem(databaseHelper.getAllHW());
                if(arrayList_new.isEmpty()){
                    Toast.makeText(getActivity(),"還沒有功課有完成過", Toast.LENGTH_LONG).show();
                }else {
                    adapter = new done_Adapter(getActivity(), arrayList_new, mList,completing_array,counting_array);
                    adapter.notifyDataSetChanged();
                    mList.setAdapter(adapter);
                }
                //停止刷新
                swipeRefreshLayout.setRefreshing(false);
            }}catch (SQLiteException e){
            e.printStackTrace();
        }

    }
}