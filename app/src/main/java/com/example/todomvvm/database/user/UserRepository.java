package com.example.todomvvm.database.user;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.todomvvm.database.AppDatabase;

import java.util.List;

public class UserRepository {
    UserDao dao;

    public UserRepository(AppDatabase appDatabase){
        dao = appDatabase.userDao();
    }

    public int authenticate(String email, String password) {
        UserEntry user = dao.authenticate(email, password);
        if (user != null) {
            return user.getId();
        }
        return 0;
    }

    public void insertUser(final UserEntry task){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertUser(task);
            }
        });
    }
}
