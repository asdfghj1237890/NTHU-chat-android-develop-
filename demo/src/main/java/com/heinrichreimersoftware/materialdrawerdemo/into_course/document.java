package com.heinrichreimersoftware.materialdrawerdemo.into_course;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.heinrichreimersoftware.materialdrawerdemo.FinalAsyncHttpClient;
import com.heinrichreimersoftware.materialdrawerdemo.MyAdapter;
import com.heinrichreimersoftware.materialdrawerdemo.MyDBHelper;
import com.heinrichreimersoftware.materialdrawerdemo.R;
import com.heinrichreimersoftware.materialdrawerdemo.course_doc_db;
import com.heinrichreimersoftware.materialdrawerdemo.item.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class document extends Fragment {
    private final String TAG = "Document";
    private Document document_unbox;
    private String title;
    private course_doc_db databaseHelper;
    private Cursor cursor;
    private ArrayList<Item> arrayList = new ArrayList<Item>();
    private MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.doc_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView cardView = (CardView) view.findViewById(R.id.card_view);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "This is a card view!", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView header = (TextView) getActivity().findViewById(R.id.doc_head_head);
        final TextView name = (TextView) getActivity().findViewById(R.id.doc_head);
        final TextView content = (TextView) getActivity().findViewById(R.id.doc_content);
        final TextView description = (TextView) getActivity().findViewById(R.id.doc_description);
        header.setText(" 教材");
        header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_archive_black_24dp,0,0,0);
        name.setText("TEXT HERE");
        content.setText("");
        description.setText("");
        //you can set the title for your toolbar here for different fragments different titles
        //getActivity().setTitle("HTML");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final TextView html_text = (TextView) getActivity().findViewById(R.id.html_content);
        String url = "http://lms.nthu.edu.tw/home.php";
        FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
        AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                String json = new String(data);
                //Log.d(TAG, "onSuccess " + json);
                //html_text.setText(json);
                //document_unbox = Jsoup.parse(json);
                //analyseHTML(document_unbox);
                //Toast.makeText(MainActivity.this, json, Toast.LENGTH_LONG).show();
            }

        });
    }
    public void loadDatabase(View v){
        RecyclerView mList = (RecyclerView) v.findViewById(R.id.list_view);
        databaseHelper = new course_doc_db(getActivity());
        try {
            databaseHelper.checkAndCopyDatabase();
            databaseHelper.openDatabase();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        try{
            cursor = databaseHelper.QueryData("select * from document");
            if(cursor != null){
                if(cursor.moveToFirst()){
                    do {
                        Item item = new Item();
                        item.setCourse_head(cursor.getString(1));
                        item.setHw_head(cursor.getString(2));
                        item.setHw_content(cursor.getString(4));
                        item.setHw_deadline(cursor.getString(3));
                        arrayList.add(item);
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
                //swipeRefreshLayout.setRefreshing(false);
            }}catch (SQLiteException e){
            e.printStackTrace();
        }

    }
}