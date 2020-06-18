package com.example.todomvvm.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.util.Date;

@Entity(tableName = "task")
public class TaskEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private int priority;
    @ColumnInfo(name="updated_at")
    private Date updatedAt;
    @ColumnInfo(name = "userId")
    private int userId;
    @ColumnInfo(name = "reminder")
    private long reminder;
    @ColumnInfo(name = "reminder_date")
    private long reminder_date;

    @Ignore
    public TaskEntry(String description, int priority, Date updatedAt, int userId, long reminder, long reminder_date) {
        this.description = description;
        this.priority = priority;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.reminder = reminder;
        this.reminder_date = reminder_date;
    }

    public TaskEntry(int id, String description, int priority, Date updatedAt, int userId, long reminder, long reminder_date) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.reminder = reminder;
        this.reminder_date = reminder_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public long getReminder() {
        return reminder;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public long getReminder_date() {
        return reminder_date;
    }

    public void setReminder_date(long reminder_date) {
        this.reminder_date = reminder_date;
    }
}
