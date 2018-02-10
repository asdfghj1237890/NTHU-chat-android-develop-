package com.google.firebase.codelab.nthuchat;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user")
    User getUser();

    @Query("SELECT * FROM user where div LIKE :div AND classes LIKE :classes")
    User findByName(String div, String classes);

    @Insert
    void insertAll(User... users);

    @Update
    void update(User user);

    @Delete
    void deleteAll(User... users);

    @Delete
    void delete(User user);
}
