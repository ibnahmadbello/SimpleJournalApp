package com.example.regent.simplejournalapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText inputEmail, inputPassword;
    private Button signUpButton, loginButton, resetPasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        signUpButton = findViewById(R.id.sign_up_button);
        loginButton =  findViewById(R.id.login_button);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        progressBar = findViewById(R.id.progress_bar);
        signInButton = findViewById(R.id.google_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        resetPasswordButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (account != null || firebaseUser != null){
            startActivity(new Intent(this, JournalActivity.class));
            finish();
        }
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
                break;
            case R.id.google_sign_in:
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            startActivity(new Intent(LoginActivity.this, JournalActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        }

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, JournalActivity.class));
            finish();
        }

    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e){
                Log.d(TAG, "Google sign in failed", e);
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, JournalActivity.class));
                        } else {
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_activity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


}
