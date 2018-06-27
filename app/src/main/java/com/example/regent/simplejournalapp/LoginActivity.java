package com.example.regent.simplejournalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail, inputPassword;
    private Button signUpButton, loginButton, resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, JournalActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton =  findViewById(R.id.login_button);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        progressBar = findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        resetPasswordButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_up_button:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                break;
            case R.id.reset_password_button:
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.login_button:
                String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()){
                                    if(password.length() < 8){
                                        inputPassword.setError("Your password is less than 8 characters");
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, JournalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
        }
    }
}
