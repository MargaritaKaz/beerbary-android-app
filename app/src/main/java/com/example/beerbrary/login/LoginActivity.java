package com.example.beerbrary.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.beerbrary.BeerlistActivity;
import com.example.beerbrary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private Button loginButton;
    private Button registerButton;
    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        emailText = (EditText) findViewById(R.id.editEmailAddress);
        passwordText = (EditText) findViewById(R.id.editPassword);
        loginButton = (Button)findViewById(R.id.loginVerifyButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRegister();
            }
        });
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, BeerlistActivity.class));
            finish();
        }
    }

    private void switchRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void signIn() {
        auth.signInWithEmailAndPassword(emailText.getText().toString(),
                passwordText.getText().toString()).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (Objects.requireNonNull(firebaseUser).isEmailVerified()) {
                    startActivity(new Intent(this, BeerlistActivity.class));
                    finish();
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                } else {
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(this, getString(R.string.please_check_email), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}