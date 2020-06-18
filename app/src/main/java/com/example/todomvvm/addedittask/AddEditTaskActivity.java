package com.example.todomvvm.addedittask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.example.todomvvm.R;
import com.example.todomvvm.database.TaskEntry;
import com.example.todomvvm.tasks.ReminderBroadcast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddEditTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddEditTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    Button timePicker;
    Button datePicker;

    long reminder;
    long reminderDate;

    Calendar commonCalendar;

    private int userId;

    private int mTaskId = DEFAULT_TASK_ID;

    AddEditTaskViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        userId = Integer.parseInt(getIntent().getStringExtra("userId"));

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);

            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI

                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getApplication(), mTaskId);
                viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);

                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });

            }
        } else {
            AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getApplication(), mTaskId);
            viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);
        }

        createNotificationChannel();

        datePicker = findViewById(R.id.txtDate);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "date picker");
            }
        });

        timePicker = findViewById(R.id.txtTime);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "time picker");
            }
        });

        commonCalendar = Calendar.getInstance();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String reminderTime = hourOfDay + ":" + minute;
        timePicker.setText(reminderTime);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        reminder = calendar.getTimeInMillis();
        commonCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        commonCalendar.set(Calendar.MINUTE, minute);
        commonCalendar.set(Calendar.SECOND, 0);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String rem = year + "-" + month + "-" + dayOfMonth;
        datePicker.setText(rem);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        reminderDate = calendar.getTimeInMillis();
        commonCalendar.set(Calendar.YEAR, year);
        commonCalendar.set(Calendar.MONTH, month);
        commonCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
        reminder = task.getReminder();
        reminderDate = task.getReminder_date();
        timePicker.setText(DateTimeFormatter("HH:mm", reminder));
        datePicker.setText(DateTimeFormatter("YYY-MM-dd", reminderDate));
    }

    private String DateTimeFormatter(String format, long millis) {
        DateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(millis));
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        // Not yet implemented
        String description = mEditText.getText().toString();
        int priority = getPriorityFromViews();
        Date date = new Date();
        TaskEntry todo = new TaskEntry(description, priority, date, userId, reminder, reminderDate);
        if (mTaskId == DEFAULT_TASK_ID) {
            viewModel.insertTask(todo);
            Intent intent = new Intent(AddEditTaskActivity.this, ReminderBroadcast.class);
            intent.putExtra("message", description);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddEditTaskActivity.this, 1, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, commonCalendar.getTimeInMillis(), pendingIntent);
        } else {
            todo.setId(mTaskId);
            viewModel.updateTask(todo);

        }
        finish();

    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        CharSequence name = "TodoApp";
        String desc = "Channel for Todo App";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("todoApp", name, importance);
        channel.setDescription(desc);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
