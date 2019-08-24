package com.example.myapplication1;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class SigninActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private EditText eusername;
    private EditText epassword;
    private EditText ecpassword;
    private Button create;
    private TextView dispdate;
    private RadioGroup sex;
    private String dateset="";
    private Spinner employment;
    String[] emp={"Employed","Unemployed","Self employed"};
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="MEH";
    Details d;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        eusername = findViewById(R.id.edtEusername);
        epassword = findViewById(R.id.edtEpassword);
        ecpassword = findViewById(R.id.edTEconfirm);
        create = findViewById(R.id.btnCreate);
        create.setOnClickListener(this);
        dispdate = findViewById(R.id.textView3);
        dispdate.setOnClickListener(this);
        sex = findViewById(R.id.radioGroup2);
        employment=findViewById(R.id.spEmployment);
        employment.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa=new ArrayAdapter<String>(SigninActivity.this,R.layout.support_simple_spinner_dropdown_item,emp);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        employment.setAdapter(aa);



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view) {
        if (view.getId() == create.getId()) {
            this.submit();
        } else if (view.getId() == dispdate.getId()) {
            this.date();
        }
    }

    public void submit() {
        if (ecpassword.getText().toString().equals(epassword.getText().toString())) {
            try{
                    Log.d(TAG,"entered try");
                    String sexselected=onclickbuttonMethod();
                     d=new Details(eusername.getText().toString(), epassword.getText().toString(),sexselected,dateset,employment.getSelectedItem());
                    authenticate();


            }
                catch(Exception e)
                {
                    if(e.getMessage().equals("no sex selected")) {
                        Toast.makeText(SigninActivity.this, "No sex selected", Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("date not set"))
                    {
                        Toast.makeText(getApplicationContext(),"birthday not set",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("username not present"))
                    {
                        Toast.makeText(getApplicationContext(),"username not entered",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("password below 8 characters"))
                    {
                        Toast.makeText(getApplicationContext(),"password below 8 characters",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("password too long"))
                    {
                        Toast.makeText(getApplicationContext(),"password above 15 characters",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("no number"))
                    {
                        Toast.makeText(getApplicationContext(),"password doesnt contain number",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("no special character"))
                    {
                        Toast.makeText(getApplicationContext(),"password doesnt contain special character",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("employment status not selected"))
                    {
                        Toast.makeText(getApplicationContext(),"employment status not selected",Toast.LENGTH_SHORT).show();
                    }
                    else if(e.getMessage().equals("The email address is already in use by another account."))
                    {
                        Toast.makeText(getApplicationContext(),"The email address is already in use by another account.",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"error occurred"+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                }
        else {
            Toast.makeText(getApplicationContext(), "Confirmed password doesnt match entered password", Toast.LENGTH_SHORT).show();
            ecpassword.getText().clear();
        }
    }

    public void date() {

        DatePickerDialog.OnDateSetListener selectdate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dateset = day + "/" + month + "/" + year;
                dispdate.setText(dateset);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(SigninActivity.this, android.R.style.Theme_Holo_Light_Dialog, selectdate, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public String onclickbuttonMethod()throws RuntimeException
    {
        int selectedId = sex.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return "";
        }
        else
        {
            if(selectedId==R.id.rbMale)
            {
                return "male";
            }
            else
            {
                return "female";
            }
        }


    }








    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //practise to show how to make custom spinners and to effectively check multiple spinners

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {


    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exiting the page will erase all details.\nAre you sure you want to close the applicaton");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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


    private void updateUI(FirebaseUser currentUser)throws RuntimeException{
        if(currentUser!=null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        else
        {
            throw new RuntimeException();
        }

    }
    public void authenticate() throws RuntimeException
    {
        Log.d(TAG,"entered authenticate");
       mAuth.createUserWithEmailAndPassword(eusername.getText().toString(), epassword.getText().toString())
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success"+FirebaseAuth.getInstance().getUid());
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> data = new HashMap<>();
                            data.put("email:", d.getUsername());
                            data.put("sex", d.getSex());
                            data.put("birthday", d.getBirthdate());
                            data.put("employment status",d.getEstatus());
                            db.collection("users").document(user.getUid())
                                    .set(d)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                            Log.d(TAG,"didnt have success");
                            updateUI(user);
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            throw new RuntimeException(task.getException().getMessage());
                        }



                    }// ...

                });


    }
}

