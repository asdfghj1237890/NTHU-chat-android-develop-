package com.heinrichreimersoftware.materialdrawerdemo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.facebook.stetho.Stetho;
import com.heinrichreimersoftware.materialdrawerdemo.item.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cz.msebera.android.httpclient.Header;
import static java.lang.Boolean.TRUE;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "NavigationActivity";
    private Document document_unbox;
    private TextView userText;
    private TextView idText;
    private TextView idhw_head;
    private ImageView profilephoto;
    private Menu coursename;
    private MyDBHelper helper;
    private course_doc_db doc_helper;
    private List<Item>  hw_list;
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private String url = "http://lms.nthu.edu.tw/home.php";
    final public ArrayList<String> course_name_list = new ArrayList();
    private FinalAsyncHttpClient finalAsyncHttpClient = new FinalAsyncHttpClient();
    private AsyncHttpClient client = finalAsyncHttpClient.getAsyncHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        helper = new MyDBHelper(NavigationActivity.this);
        recyclerView =(RecyclerView) findViewById(R.id.list_view);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        File delFile0 = new File(Environment.getExternalStorageDirectory().getParent() + "/" + Environment.getExternalStorageDirectory().getName() + "/iLMS-DATA/course_link.txt");
        File delFile1 = new File(Environment.getExternalStorageDirectory().getParent() + "/" + Environment.getExternalStorageDirectory().getName() + "/iLMS-DATA/course_name.txt");
        try{
            delFile0.delete();
            //delFile1.delete();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        coursename = navigationView.getMenu();
        coursename.clear();
        coursename.add(0,1314,0,"作業");
        final SubMenu sub1 = coursename.addSubMenu("Course");
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                String json = new String(data);
                document_unbox = Jsoup.parse(json);
                analyseName(document_unbox);
                analyseID(document_unbox);
                long deletedb = helper.getWritableDatabase().delete("exp",null,null);
                Log.d("ADD", deletedb+"");
                //analysepropic(document_unbox);
                analysecourse(document_unbox,sub1);
            }
        });

        coursename.findItem(1314).setIcon(R.drawable.ic_assignment_black_24dp_1x);
        SubMenu sub2 = coursename.addSubMenu("Setting");
        sub2.add(0,404,0,"登出");
        MenuItem logoutItem = sub2.findItem(404);
        logoutItem.setIcon(R.drawable.ic_power_settings_new_black_24dp_1x);

        //displaySelectedScreen(R.id.nav_assignment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(id);

        if (id == 1314) {
            //displaySelectedScreen(id);
        } else if (id == 404) {
            CookieUtils.clearCookie(NavigationActivity.this);
            Toast.makeText(NavigationActivity.this, "Signing out" , Toast.LENGTH_SHORT).show();
            finish();
            Intent myIntent = new Intent(NavigationActivity.this,LoginActivity.class);
            NavigationActivity.this.startActivity(myIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
        return true;
    }
    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            /*case 1:
                fragment = new Menu1();
                break;*/
            case 1314:
                fragment = new assignment();
                break;
            default:
                String name = coursename.findItem(itemId).toString();
                //Toast.makeText(NavigationActivity.this, itemId+"" , Toast.LENGTH_SHORT).show();
                //Toast.makeText(NavigationActivity.this, name , Toast.LENGTH_SHORT).show();
                fragment = new course_item(name);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    public void analyseName(Document document){
        userText = (TextView)findViewById(R.id.usernametext);
        if (document!=null){
            Elements elements = document.select("div#profile");
            String[] title = elements.get(0).text().split(" ");
            //Log.d(TAG, title[1]);
            userText.setText(title[1]);
        }
    }
    public void analyseID(Document document){
        idText = (TextView)findViewById(R.id.idtext);
        if (document!=null){
            Elements elements = document.select("div.mnuTitle");
            String[] title = elements.get(2).text().split(" ");
            //Log.d(TAG, title[1]);
            idText.setText(title[0]);
        }
    }
    public void findHW(String course_url,final int seq_order){
        client.get(course_url, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                ContentValues values = new ContentValues();
                String json = new String(data);
                document_unbox = Jsoup.parse(json);
                //idhw_head = (TextView)findViewById(R.id.html_head_head);
                if (document_unbox!=null){
                    //Elements elements = document_unbox.select("div.boxBody>div.Ehomework>a[title]");
                    Elements hw_elements = document_unbox.select("div.tableBox>table>tbody>tr");
                    Elements name_elements = document_unbox.select("div.infoPath>a");
                    if(hw_elements.size() > 0) {
                        for (int i = 1; i < hw_elements.size(); i++) {
                            Elements hw_elements_select = hw_elements.get(i).select("td>a");
                            Elements deadline_date = hw_elements.get(i).select("td>span[title]");
                            String class_title = name_elements.get(0).text();
                            String hw_title = hw_elements_select.text();
                            String deadline_text = deadline_date.attr("title");
                            //Toast.makeText(NavigationActivity.this, hw_elements.get(i).text(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(NavigationActivity.this, deadline_text, Toast.LENGTH_SHORT).show();
                            values.put("course_name",class_title );
                            values.put("hw_name", hw_title);
                            values.put("deadline_date", deadline_text);
                            values.put("finish", 0);
                            values.put("content", "TESTING content");
                            long id = helper.getWritableDatabase().insert("exp", null, values);
                            Log.d("ADD", id+"");
                        }
                    }
                }
                /*values.put("course_name","GGG");
                values.put("hw_name", "GGG");
                values.put("deadline_date", "GGG");
                values.put("finish", 0);
                values.put("content", "TESTING content");
                long id = helper.getWritableDatabase().insert("exp", null, values);
                Log.d("ADD", id+"");*/
            }
        });
    }

    public void finddoc(String course_url,final int seq_order){
        client.get(course_url, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                ContentValues values = new ContentValues();
                String json = new String(data);
                document_unbox = Jsoup.parse(json);
                //idhw_head = (TextView)findViewById(R.id.html_head_head);
                if (document_unbox!=null){
                    //Elements elements = document_unbox.select("div.boxBody>div.Ehomework>a[title]");
                    Elements doc_elements = document_unbox.select("div.tableBox>table>tbody>tr");
                    Elements name_elements = document_unbox.select("div.infoPath>a");
                    if(doc_elements.size() > 0) {
                        for (int i = 1; i < doc_elements.size(); i++) {
                            Elements doc_elements_select = doc_elements.get(i).select("td>a");
                            //Elements cid_select = doc_elements.get(i).select("td.td");
                            Elements date = doc_elements.get(i).select("td>span");
                            String class_title = name_elements.get(0).text();
                            String doc_title = doc_elements_select.text();
                            String deadline_text = date.text();
                            String link_text = doc_elements_select.attr("href");
                            //Toast.makeText(NavigationActivity.this, hw_elements.get(i).text(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(NavigationActivity.this, hw_elements_select.text(), Toast.LENGTH_LONG).show();
                            values.put("link",link_text );
                            values.put("doc_name", doc_title);
                            values.put("class_name", class_title);
                            values.put("date", deadline_text);
                            //values.put("discuss", );
                            values.put("content", "TESTING content");
                            long id = doc_helper.getWritableDatabase().insert("exp", null, values);
                            Log.d("ADD", id+"");
                        }
                    }
                }
            }
        });
    }

    public void analysepropic(Document document){
        profilephoto = (ImageView)findViewById(R.id.imageView);
        if (document!=null){
            Elements elements = document.select("div#profile>div>img");
            String title = elements.attr("src");
            final String mixtitle = "http://lms.nthu.edu.tw"+title;
            Log.d(TAG, mixtitle);
            //Toast.makeText(NavigationActivity.this, mixtitle, Toast.LENGTH_LONG).show();
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap mBitmap = getBitmapFromURL(mixtitle);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilephoto.setImageBitmap(mBitmap);
                        }
                    });
                }
            }).start();*/
        }
    }
    public static Bitmap getBitmapFromURL(String src){
        try{
            URL url = new URL(src);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.connect();

            InputStream input = conn.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);
            return mBitmap;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
    public void analysecourse(Document document,SubMenu menu){
            Elements elements = document.select("div.mnuItem>a");
            String final_name = "";
            File mSDFile = null;
            String[] array = {};
            //array = readInfo("course_hw","course4/");
            //檢查有沒有SD卡裝置
            if(!Environment.getExternalStorageState().equals( Environment.MEDIA_REMOVED)) {
                for(int dd = 0; dd < elements.size()-1; dd++){
                    File delFile_file = new File(Environment.getExternalStorageDirectory().getParent() + "/" + Environment.getExternalStorageDirectory().getName()+ "/iLMS-DATA/course"+dd+"/course_hw.txt");
                    if(!delFile_file.exists())
                    {
                        //delFile.mkdirs();
                    }
                    else
                        try{
                            //delFile.delete();
                            FileWriter delFile = new FileWriter(Environment.getExternalStorageDirectory().getParent() + "/" + Environment.getExternalStorageDirectory().getName()+ "/iLMS-DATA/course"+dd+"/course_hw.txt");
                            PrintWriter pw = new PrintWriter(delFile);
                            pw.write("");
                            pw.flush();
                            pw.close();
                            System.out.println("File!!! Cleared");
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                }
                //取得SD卡儲存路徑
                mSDFile = Environment.getExternalStorageDirectory();
                //檢查文件檔儲存路徑
                File mFile = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/iLMS-DATA/course_link.txt");
                File mFile0 = new File(mSDFile.getParent() + "/" + mSDFile.getName() + "/iLMS-DATA/course_name.txt");
                if(mFile.exists()){
                    for (int i = 0; i < elements.size()-1 ; i++) {
                        final_name = "";
                        String[] title = elements.get(i).text().split("");
                        //Log.d(TAG, title);
                        for (int m = 0; m < title.length; m++) {
                            if (title[m].matches("[A-Za-z0-9() ]*")) {
                                title[m] = "";
                            } else {
                                final_name += title[m];
                            }
                        }
                        //String src_title = elements.get(i).attr("href");
                        //final String mixtitle = "http://lms.nthu.edu.tw" + src_title + "\n";
                        //writeInfo("course_link", mixtitle);
                        menu.add(0, i+1, 0, final_name);
                        //Toast.makeText(NavigationActivity.this, title, Toast.LENGTH_LONG).show();
                        menu.findItem(i+1).setIcon(R.drawable.ic_find_in_page_black_24dp_1x);
                    }
                }
                else {
                    for (int i = 0; i < elements.size()-1; i++) {
                        final_name = "";
                        String[] title = elements.get(i).text().split("");
                        //Log.d(TAG, title);
                        for (int m = 0; m < title.length; m++) {
                            if (title[m].matches("[A-Za-z0-9() ]*")) {
                                title[m] = "";
                            } else {
                                final_name += title[m];
                            }
                        }
                        String src_title = elements.get(i).attr("href");
                        String course_id = src_title.split("/")[2];
                        String course_hw_list = "http://lms.nthu.edu.tw/course.php?courseID="+course_id+"&f=hwlist";
                        String course_doc_list = "http://lms.nthu.edu.tw/course.php?courseID="+course_id+"&f=doclist";
                        final String mixtitle_withoutn = "http://lms.nthu.edu.tw" + src_title;
                        findHW(course_hw_list,i);
                        //finddoc(course_doc_list,i);
                        //Toast.makeText(NavigationActivity.this, course_id, Toast.LENGTH_SHORT).show();
                        menu.add(0, i+2, 0, final_name);
                        menu.findItem(i+2).setIcon(R.drawable.ic_find_in_page_black_24dp_1x);

                    }
                    mFile0.setReadOnly();
                    //Toast.makeText(NavigationActivity.this, array[0], Toast.LENGTH_SHORT).show();
                    Toast.makeText(NavigationActivity.this, "已截取課程資料", Toast.LENGTH_SHORT).show();
                    Toast.makeText(NavigationActivity.this, "已截取功課資料", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(NavigationActivity.this, readInfo("course_link"), Toast.LENGTH_SHORT).show();
                }
            }
    }
}
