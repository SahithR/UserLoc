package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loc=findViewById(R.id.btnloc);
        loc.setOnClickListener(this);
        Button signout=findViewById(R.id.btnSignOut);
        signout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnloc)
        {
            Intent i=new Intent(LoginActivity.this,CurrentLocationActivity.class);
            startActivity(i);
        }
        else if(view.getId()==R.id.btnSignOut)
        {
            FirebaseAuth.getInstance().signOut();
            Intent i=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exiting the page will sign you out.\nAre you sure you want to continue with the action");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Action for 'NO' Button
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Back button pressed");
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }
}
