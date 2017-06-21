package com.heinrichreimersoftware.materialdrawerdemo;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.heinrichreimersoftware.materialdrawerdemo.item.Item;

public class MyDBHelper extends SQLiteOpenHelper{
    private static String DB_NAME = "main.exp";
    private static String DB_PATH = "";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public MyDBHelper(Context context) {
        super(context, DB_NAME, null,1);
        if(Build.VERSION.SDK_INT >= 15){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }else{
            DB_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases";
        }
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ DB_NAME  +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                //"cdate DATETIME NOT NULL , " +
                "course_name VARCHAR, " +
                "course_id VARCHAR, " +
                "hw_name VARCHAR, " +
                "hw_id VARCHAR, " +
                //"info VARCHAR, " +
                "deadline_date VARCHAR, " +
                "finish INTEGER, "+
                //"amount INTEGER)");
                "content VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            onCreate(db);
        }
    }

    public void checkAndCopyDatabase(){
        boolean dbExist = checkDatabase();
        if (dbExist){
            Log.d("TAG","database already exist");
        }else{
            this.getReadableDatabase();
        }
        try {
            copyDatabase();
        }catch (IOException e){
            e.printStackTrace();
            Log.d("TAG","Error copy database");
        }
    }

    public void copyDatabase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0){
            //myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDatabase(){
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    public boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteException e){

        }
        if (checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public synchronized void close(){
        if(myDatabase != null){
            myDatabase.close();
        }
        super.close();
    }

    public Cursor QueryData(String query){
        return myDatabase.rawQuery(query,null);
    }

    public List<Item> getAllHW(){
        List<Item> hwList=new ArrayList<Item>();
        String selectQuery="SELECT * FROM "+DB_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                Item hw=new Item();
                hw.setId(Integer.parseInt(cursor.getString(0)));
                hw.setCourse_head(cursor.getString(1));
                hw.setHw_head(cursor.getString(2));
                hw.setHw_content(cursor.getString(4));
                hw.setHw_deadline(cursor.getString(3));
                hwList.add(hw);
            }while(cursor.moveToNext());
        }
        return hwList;
    }
}
