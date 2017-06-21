package com.heinrichreimersoftware.materialdrawerdemo.hw_am;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.heinrichreimersoftware.materialdrawerdemo.MyAdapter;
import com.heinrichreimersoftware.materialdrawerdemo.MyDBHelper;
import com.heinrichreimersoftware.materialdrawerdemo.R;
import com.heinrichreimersoftware.materialdrawerdemo.item.Item;

public class undoam extends Fragment {
    private RecyclerView mList;
    private MyDBHelper databaseHelper;
    private ArrayList<Item> arrayList = new ArrayList<Item>();
    private Cursor cursor;
    private MyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        final View undoView = inflater.inflate(R.layout.undoam, container, false);
        RecyclerView mList = (RecyclerView) undoView.findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) undoView.findViewById(R.id.refresh);
        loadDatabase(undoView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);
        adapter = new MyAdapter(getActivity(),arrayList,mList);
        adapter.notifyDataSetChanged();
        //設置刷新監聽
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDatabase(undoView);
            }
        });

        mList.setAdapter(adapter);
        //final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //MyAdapter myAdapter = new MyAdapter(myHeadset,myDateset,mList);
        //MyAdapter myAdapter = new MyAdapter(mList);
        //mList.setAdapter(myAdapter);
        return undoView;
    }

    public void loadDatabase(View v){
        arrayList.clear();
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
            if(cursor.moveToFirst()){
                do {
                    Item item = new Item();
                    item.setCourse_head(cursor.getString(1));
                    item.setHw_head(cursor.getString(3));
                    item.setHw_content(cursor.getString(7));
                    item.setHw_deadline(cursor.getString(5));
                    try{
                        Date parsed_date = system_date.parse(cursor.getString(5));
                        Date now = new Date(System.currentTimeMillis());
                        //Toast.makeText(getActivity(),cursor.getString(2)+parsed_date.compareTo(now), Toast.LENGTH_LONG).show();
                        if(parsed_date.compareTo(now) == 1) arrayList.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }while (cursor.moveToNext());
            }
            //adapter.refreshItem(databaseHelper.getAllHW());
            if(arrayList.isEmpty()){
                //Toast.makeText(getActivity(),"EMPTY", Toast.LENGTH_LONG).show();
                Item item = new Item();
                item.setCourse_head("UCCU");
                item.setHw_head("現在沒有功課呀，別看啦");
                item.setHw_deadline("一萬年");
                item.setHw_content("ლ(・´ｪ`・ლ)");
                arrayList.add(item);
            }else {
                adapter = new MyAdapter(getActivity(), arrayList, mList);
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
