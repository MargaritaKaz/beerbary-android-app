package com.example.beerbrary.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beerbrary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private Button registerButton;
    private EditText emailText;
    private EditText passwordText;
    private EditText usernameText;
    private EditText passwordRepeatText;
    private Button goBackButton;
    ArrayList<String> usernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        usernames = new ArrayList<>();
        emailText = (EditText) findViewById(R.id.editEmailAddressRegister);
        passwordText = (EditText) findViewById(R.id.editPasswordRegister);
        usernameText = (EditText) findViewById(R.id.editUsernameRegister);
        passwordRepeatText = (EditText) findViewById(R.id.editPasswordRepeatRegister);
        registerButton = (Button) findViewById(R.id.registerVerifyButton);
        usernamesSetter();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWrapper();
            }
        });
        goBackButton = (Button) findViewById(R.id.registerGoBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void usernamesSetter() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> usernames = new ArrayList<>();
                    for (DataSnapshot snap : task.getResult().getChildren()) {
                        usernames.add(snap.getValue().toString());
                    }
                    setUsernames(usernames);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setUsernames(ArrayList<String> names) {
        this.usernames = names;
    }

    private void goBack() {
        startActivity(new Intent(this, LoginActivity.class));
    }


    private void registerWrapper() {
        String text1 = emailText.getText().toString();
        String text2 = passwordText.getText().toString();
        String text3 = passwordRepeatText.getText().toString();
        String text4 = usernameText.getText().toString();
        if (!text4.isEmpty() && text4 != "" && !text1.isEmpty() && text1 != "" && !text2.isEmpty()) {
            if (text2 != "" && !text3.isEmpty() && text3 != "" && text2.equals(text3)) {
                boolean found = false;
                for (String s : this.usernames) {
                    if (s.equals(text4)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    register(text1, text2);
                } else {
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void register(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String new_user = auth.getCurrentUser().getUid();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
                db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            db.child(new_user).setValue(usernameText.getText().toString());
                        } else {
                            System.out.println("error");
                        }
                    }
                });
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(this, getString(R.string.user_sign_up_success), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
    }
}
