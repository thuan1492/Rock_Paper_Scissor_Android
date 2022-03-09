package com.example.androidgroupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePass extends AppCompatActivity {
    private Button btnSave;
    private TextView exit;
    private EditText oldPassword, newPassword;

    protected FirebaseAuth mAuth;
    protected FirebaseUser user;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;
    protected AuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        btnSave = (Button) findViewById(R.id.btnChangePass);
        exit = (TextView) findViewById(R.id.exit);
        oldPassword = (EditText) findViewById(R.id.oldPass);
        newPassword = (EditText) findViewById(R.id.newPass);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChangePass.this,MainAction.class));
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass = newPassword.getText().toString();
                String oldPass = oldPassword.getText().toString();
                if(newPass.isEmpty() || oldPass.isEmpty()) {
                    Toast.makeText(ChangePass.this, "Enter passwords...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    UpdatePassword(newPass, oldPass);
                }
            }
        });
    }

    private void UpdatePassword(String newPass, String oldPass) {
        if(user != null) {
            String email_id = user.getEmail();
            DocumentReference email = siteref.document(email_id);
            CollectionReference profile = email.collection("Profile");
            DocumentReference currentPass = profile.document("Basic Information");
            currentPass.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String currentPassword = documentSnapshot.getString("Password");
                    if(currentPassword.equals(oldPass)) {
                        credential = EmailAuthProvider.getCredential(email_id,currentPassword);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                System.out.println("updated success");
                                                SavePassword(newPass);
                                                Toast.makeText(ChangePass.this, "Success",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangePass.this, "Wrong old password",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SavePassword(String newPass) {
        user = mAuth.getCurrentUser();
        DocumentReference ref = userinfo.collection("users")
                .document(user.getEmail())
                .collection("Profile")
                .document("Basic Information");
        ref.update("Password",newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("updated");
                startActivity(new Intent(ChangePass.this,MainAction.class));
                finish();
            }
        });

    }
}