package com.example.todomvvm.database.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntry userEntry);

    @Query("Select * from users where email=:email and password=:password")
    UserEntry authenticate(String email, String password);
}
