package com.heinrichreimersoftware.materialdrawerdemo.into_course;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.heinrichreimersoftware.materialdrawerdemo.FinalAsyncHttpClient;
import com.heinrichreimersoftware.materialdrawerdemo.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import cz.msebera.android.httpclient.Header;

public class group extends Fragment {
    private final String TAG = "Menu1";
    private Document document_unbox;
    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.gro_layout, container, false);
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
        final TextView header = (TextView) getActivity().findViewById(R.id.gro_head_head);
        final TextView name = (TextView) getActivity().findViewById(R.id.gro_head);
        final TextView content = (TextView) getActivity().findViewById(R.id.gro_content);
        final TextView description = (TextView) getActivity().findViewById(R.id.gro_description);
        header.setText(" 小組");
        header.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dns_black_24dp,0,0,0);
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
    public void analyseHTML(Document document){
        if (document!=null){
            Elements elements = document.select("div#profile");
            String[] title = elements.get(0).text().split(" ");
            //Log.d(TAG, title[1]);
        }
    }
}