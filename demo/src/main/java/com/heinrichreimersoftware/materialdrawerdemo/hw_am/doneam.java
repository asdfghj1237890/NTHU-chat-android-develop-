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
import com.heinrichreimersoftware.materialdrawerdemo.done_Adapter;
import com.heinrichreimersoftware.materialdrawerdemo.MyDBHelper;
import com.heinrichreimersoftware.materialdrawerdemo.R;
import com.heinrichreimersoftware.materialdrawerdemo.item.Item;
import com.heinrichreimersoftware.materialdrawerdemo.item.done_progress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class doneam extends Fragment {
    private RecyclerView mList;
    private MyDBHelper databaseHelper;
    private ArrayList<done_progress> arrayList_new = new ArrayList<done_progress>();
    private Cursor cursor;
    private done_Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        adapter = new done_Adapter(getActivity(),arrayList_new,mList);
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
        SimpleDateFormat system_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat system_time = new SimpleDateFormat("hh:mm");
        //String date = system_date.format(new Date());
        //String time = system_time.format(new Date());
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
                if(cursor.moveToFirst()) {
                    String course_title = cursor.getString(1);
                    while (cursor.isLast() != true) {
                        if (course_title.equals(cursor.getString(1)) == false) {
                            Log.d("title", course_title);
                            Log.d("getString", cursor.getString(1));
                            Log.d("id", cursor.getPosition()+"");
                            if(cursor.getPosition() <= 0){
                                cursor.moveToNext();
                            }
                            else {
                                cursor.moveToPrevious();
                                Log.d("id", cursor.getPosition() + "");
                                done_progress done = new done_progress();
                                done.setCourse_head(cursor.getString(1));
                                course_title = cursor.getString(1);
                                arrayList_new.add(done);
                                cursor.moveToNext();
                                cursor.moveToNext();
                            }
                        }
                        else{cursor.moveToNext();}
                    }
                }
                //adapter.refreshItem(databaseHelper.getAllHW());
                if(arrayList_new.isEmpty()){
                    Toast.makeText(getActivity(),"還沒有功課有完成過", Toast.LENGTH_LONG).show();
                }else {
                    adapter = new done_Adapter(getActivity(), arrayList_new, mList);
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