package com.google.firebase.codelab.nthuchat;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Query;

import java.util.List;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "div")
    private String Div;

    @ColumnInfo(name = "classes")
    private String Classes;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDiv() {
        return Div;
    }

    public void setDiv(String div) {
        this.Div = div;
    }

    public String getClasses() {
        return Classes;
    }

    public void setClasses(String classes) {
        this.Classes = classes;
    }

}