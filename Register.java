package com.example.androidgroupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import android.app.ProgressDialog;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {
    protected StorageReference store_image;
    private Vibrator vibe;
    final Calendar myCalendar = Calendar.getInstance();
    //private static final String TAG = SignUp.class.getSimpleName() ;
    EditText newpass, confpass, newemail, user_name, phone, user_birth;
    Button signup;
    TextView have_acc;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    protected FirebaseFirestore db;
    DatePickerDialog.OnDateSetListener date;
    ImageView profile_pic;
    Uri image_uri;
    private ProgressDialog process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_register);
        newpass=(EditText)findViewById(R.id.new_password);
        confpass=(EditText)findViewById(R.id.confirm_password);
        newemail=(EditText)findViewById(R.id.register_email);
        user_name=(EditText)findViewById(R.id.user_name);
        user_birth=(EditText)findViewById(R.id.user_birthday);
        phone=(EditText)findViewById(R.id.phone_number);
        profile_pic =(ImageView) findViewById(R.id.add_pic);
//        calendar =(ImageView) findViewById(R.id.calendar);
        signup=(Button)findViewById(R.id.register);
        have_acc=(TextView) findViewById(R.id.have_acc);



        signup.setOnClickListener(this);
        have_acc.setOnClickListener(this);
        profile_pic.setOnClickListener(this);
//        calendar.setOnClickListener(this);
        user_birth.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        store_image = FirebaseStorage.getInstance().getReference();
        process = new ProgressDialog(Register.this);
        //initialize the calendar on click
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

    }

    //set the date time format
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());

        user_birth.setText(simpleDateFormat.format(myCalendar.getTime()));

    }

    // manage the onClick activity
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.have_acc){
            startActivity(new Intent(Register.this, Login.class));
            vibe.vibrate(100);
            finish();
        }
        else if(v.getId() == R.id.register){

            signUpUser(newemail.getText().toString(),newpass.getText().toString());
            vibe.vibrate(100);
        }
        else if(v.getId() == R.id.add_pic){
            SelectProfilePic();
        }
//        else if(v.getId() == R.id.calendar){
//
//            new DatePickerDialog(SignUp.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        }
        else if(v.getId() == R.id.user_birthday){

            new DatePickerDialog(Register.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }

    }

    //handle the profile selection of user
    private void SelectProfilePic() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    // check permission for Camera and Storage of user
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission,1000);
                        }
                        else {
                            openCamera();
                        }
                    }
                    else {
                        openCamera();
                    }
                }
                else if (options[item].equals("Choose from Gallery")){

                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }

    //handle Camera action and store the picture to user's storage
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //Camera intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(takePictureIntent, 1);

    }

    //handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else {
                    //permisiion from pop up was denied.
                    Toast.makeText(Register.this,"Permission Denied...",Toast.LENGTH_LONG).show();
                }
        }
    }

    /*------------ Below Code is for successful sign up process -----------*/
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    profile_pic.setImageURI(image_uri);
                    break;
                case 2:
                    //data.getData returns the content URI for the selected Image
                    image_uri = data.getData();
                    profile_pic.setImageURI(image_uri);
                    break;
            }
        }
    }

    // check valid input of user
    private void signUpUser(String email, String password) {

        if(email.equals("")){
            Toast.makeText(Register.this, "Enter Email!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(user_birth.getText().toString().equals("")){
            Toast.makeText(Register.this, "Enter your birthday!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(password.equals("")){
            Toast.makeText(Register.this, "Enter Password!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(!(password.equals(confpass.getText().toString())))
        {
            Toast.makeText(Register.this, "Password was not match!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(!phone.getText().toString().matches("\\d{10}"))
        {
            Toast.makeText(Register.this, "Phone number is wrong format!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if((user_name.getText().toString()).equals("")){
            Toast.makeText(Register.this, "Enter Name!!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(image_uri == null){
            Toast.makeText(Register.this, "Select Profile!!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            process.setTitle("Completing the Sign Up....");
            process.show();
            //when all information are valid
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            userProfile(email, password);
//                            Toast.makeText(SignUp.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    process.dismiss();
//                            Toast.makeText(SignUp.this, "Failed to Sign up", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /*----------For saving image and user name in Firebase Database-------*/
    private void userProfile(String email, String password) {
        user = mAuth.getCurrentUser();
        String uphotos = "Users";
        String profile = "Profiles";


        if(user != null){
            // Create a new user with a first, middle, and last name
            Map<String, Object> userinf = new HashMap<>();
            userinf.put("Name", user_name.getText().toString());
            userinf.put("Phone", phone.getText().toString());
            userinf.put("Address", "");
            userinf.put("Gender", "");
            userinf.put("Birthday", user_birth.getText().toString());
            userinf.put("Password", password);
            // Add a new document with a generated ID
            db.collection("users").document(email).collection("Profile").document("Basic Information").set(userinf)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "Stored", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Some thing Wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
            try {
                StorageReference mstore_profile  = store_image.child(uphotos+"/"+email+"/"+ profile +"/"+ user_name.getText().toString() +".jpg");//.child(user_name.getText().toString()+image_uri.getLastPathSegment());
                mstore_profile.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(Register.this, "Stored Image", Toast.LENGTH_SHORT).show();
//                        verifyEmailRequest();
                    }
                });
//                new OnSuccessListener() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(SignUp.this, "Stored Image", Toast.LENGTH_SHORT).show();
//                        verifyEmailRequest();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SignUp.this, "Failed to store Image", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
            catch (NullPointerException nullPointerException){
            }
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(user_name.getText().toString())
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    verifyEmailRequest();
                }
            });
        }
    }

    /*-------For sending verification email on user's registered email------*/
    private void verifyEmailRequest() {
        user.sendEmailVerification()
                .addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            process.dismiss();
                            Toast.makeText(Register.this,"Email verification sent on\n"+newemail.getText().toString(),Toast.LENGTH_LONG).show();

                            startActivity(new Intent(Register.this,Login.class));
                            finish();
                        }
                        else {
                            process.dismiss();
                            Toast.makeText(Register.this,"Sign up Success But Failed to send verification email.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void Logout_activity(){
        final CharSequence[] options = {"Yes", "No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle("Do you want to CANCLE the register ?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Yes")) {
                    startActivity(new Intent(Register.this,Login.class));
                    finish();
                } else if (options[item].equals("No")) {

                    dialog.dismiss();

                }
            }
        });
        builder.show();

    }
    @Override
    public void onBackPressed() {
        Logout_activity();
    }
}