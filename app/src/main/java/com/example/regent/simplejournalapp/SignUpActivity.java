package com.example.regent.simplejournalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword, btnSkip;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        btnResetPassword = findViewById(R.id.reset_password_button);
        btnSkip = findViewById(R.id.skip_button);
        progressBar = findViewById(R.id.progress_bar);

        btnResetPassword.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnSkip.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reset_password_button:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
            case R.id.sign_in_button:
                Log.d(TAG, "SIGN IN CLICKED");
                try {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                } catch (Exception e){
                    Log.d(TAG, "Error message: " + e.getMessage());
                }
                break;
            case R.id.sign_up_button:
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8){
                    Toast.makeText(getApplicationContext(),
                            "Password too short, enter minimum of 8 characters!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                // Create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this,
                                        "createUserWithEmail:onComplete:" + task.isSuccessful(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()){
                                    Toast.makeText(SignUpActivity.this,
                                            "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignUpActivity.this, JournalActivity.class));
                                    finish();
                                }
                            }
                        });
                break;
            case R.id.skip_button:
                startActivity(new Intent(this, JournalActivity.class));
                break;
        }
    }
}
