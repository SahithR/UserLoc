package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText password;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.edtUsername);
        password = findViewById(R.id.edtPassword);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignOut).setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "User logged in ", Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(I);
                }
            }

        };
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnSignOut) {
                Intent signinchange = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(signinchange);
                username.setText("");
                password.setText("");
        }
        else if (view.getId() == R.id.btnLogin) {
            firebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Wrong details", Toast.LENGTH_SHORT).show();
                        username.setText("");
                        password.setText("");
                    } else {
                        Intent loginchange = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginchange);
                        username.setText("");
                        password.setText("");
                    }
                }
            });
        }
        }

    }

