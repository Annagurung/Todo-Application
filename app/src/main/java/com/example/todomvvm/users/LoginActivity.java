package com.example.todomvvm.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todomvvm.R;
import com.example.todomvvm.database.AppDatabase;
import com.example.todomvvm.database.user.UserRepository;
import com.example.todomvvm.tasks.MainActivity;

public class LoginActivity extends AppCompatActivity {
    TextView signUp, email, password;
    AppDatabase appDatabase;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appDatabase = AppDatabase.getInstance(this);
        userRepository = new UserRepository(appDatabase);
        signUp = findViewById(R.id.txtSignUp);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        String signUpText = "Don't have account? Sign Up";
        SpannableString spanOpenSignUp = new SpannableString(signUpText);

        ClickableSpan openSignUpActivity = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        };

        spanOpenSignUp.setSpan(openSignUpActivity, 20, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setText(spanOpenSignUp);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class AsyncLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int userId = userRepository.authenticate(email.getText().toString(), password.getText().toString());
            if (userId != 0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userId", Integer.toString(userId));
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    public void login(View view) {
        new AsyncLogin().execute();
    }
}