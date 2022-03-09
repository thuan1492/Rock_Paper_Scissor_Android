package com.example.androidgroupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Vibrator vibe;
    protected FirebaseAuth mAuth ;
    protected FirebaseUser user ;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;

    TextView signup, forgot;
    EditText email, password;
    Button login;
    private ProgressDialog process;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_login);
        //Set UI ID
        signup = (TextView) findViewById(R.id.signup);
        forgot = (TextView) findViewById(R.id.forgot);
        login = (Button) findViewById(R.id.login);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgot.setOnClickListener(this);
        process = new ProgressDialog(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(user);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signup){
            startActivity(new Intent(Login.this,Register.class));
            vibe.vibrate(100);
            finish();
        }
        else if(v.getId() == R.id.forgot){
            String mail = email.getText().toString();
            if(mail.isEmpty()) {
                Toast.makeText(Login.this, "Enter your email",
                        Toast.LENGTH_SHORT).show();
            } else{
                ResetPassword(mail);
            }

            vibe.vibrate(100);
        }
        else if(v.getId() == R.id.login){

            loginUser(email.getText().toString(),password.getText().toString());
            vibe.vibrate(100);
        }
    }

    private void ResetPassword(String email) {
        String emailAddress = email;

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //check the back pressed
    private void exit(){
        final CharSequence[] options = {"Yes", "No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Do you want to EXIT ?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Yes")) {
                    mAuth.signOut();
                    finish();
                } else if (options[item].equals("No")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*------------ Below Code is for successful login process -----------*/
    private void loginUser(String email, String password) {
        if(email.equals("")){
            Toast.makeText(Login.this, "Enter Email!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(password.equals("")){
            Toast.makeText(Login.this, "Enter Password!!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            process.setTitle("Getting Data of User...");
            process.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DocumentReference ref = userinfo.collection("users")
                                        .document(email)
                                        .collection("Profile")
                                        .document("Basic Information");
                                ref.update("Password",password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        System.out.println("Saved new password");
                                        // Sign in success, update UI with the signed-in user's information
                                        updateUI(user);
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    // check the user's account is verified or not.
    private void updateUI(FirebaseUser user) {
        user = mAuth.getCurrentUser();
        /*-------- Check if user is already logged in or not--------*/
        if (user != null) {
            /*------------ If user's email is verified then access login -----------*/
            if(user.isEmailVerified()){
                Toast.makeText(Login.this, "Login Success.",
                        Toast.LENGTH_SHORT).show();
                process.dismiss();
                startActivity(new Intent(Login.this,MainAction.class));
                finish();
            }
            else {
                process.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}