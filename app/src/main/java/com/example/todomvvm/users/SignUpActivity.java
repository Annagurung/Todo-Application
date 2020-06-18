package com.example.todomvvm.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.example.todomvvm.R;
import com.example.todomvvm.database.AppDatabase;
import com.example.todomvvm.database.user.UserEntry;
import com.example.todomvvm.database.user.UserRepository;

public class SignUpActivity extends AppCompatActivity {

    TextView name, email, password, login;
    AppDatabase appDatabase;
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        appDatabase = AppDatabase.getInstance(this);
        userRepository = new UserRepository(appDatabase);
        name = findViewById(R.id.txtName);
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        login = findViewById(R.id.txtLogin);
        String loginText = "Already a member? Login";
        SpannableString spanOpenLogin = new SpannableString(loginText);

        ClickableSpan openLoginActivity = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };

        spanOpenLogin.setSpan(openLoginActivity, 18, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spanOpenLogin);
        login.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void register(View view) {
        UserEntry userEntry = new UserEntry(name.getText().toString(), email.getText().toString(), password.getText().toString());
        userRepository.insertUser(userEntry);
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}