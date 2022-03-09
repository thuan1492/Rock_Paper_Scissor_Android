package com.example.androidgroupproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainAction extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Vibrator vibe ;
    private DrawerLayout drawer;
    protected FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected FirebaseStorage profilestorage;
    protected FirebaseFirestore userinfo = FirebaseFirestore.getInstance();
    protected CollectionReference siteref = userinfo.collection("users");
    private ProgressDialog process;
    View headerView;
    ImageView uprofile;
    TextView uname, uemail;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_main_action);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //process = new ProgressDialog(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_account);
        }

        uname = (TextView) headerView.findViewById(R.id.user_name);
        uemail = (TextView) headerView.findViewById(R.id.user_email);
        uprofile = (ImageView) headerView.findViewById(R.id.user_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user!=null){
            String name = user.getDisplayName();
            String email_id = user.getEmail();
            //Uri image_uri = user.getPhotoUrl();
            String uphotos = "Users";
            String profile_fol = "Profiles";
            DocumentReference email = siteref.document(email_id);
            CollectionReference profile = email.collection("Profile");
            DocumentReference basicinfo = profile.document("Basic Information");
            //process.setTitle("Loading...");
            basicinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                   // process.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  //  process.dismiss();
                }
            });

            uname.setText(name);
            uemail.setText(email_id);

            StorageReference profilestorage = FirebaseStorage.getInstance().getReference().child(uphotos+"/"+email_id+"/"+ profile_fol +"/"+ name +".jpg");
            profilestorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        System.out.println("hello");
                        Uri downloadUrl = task.getResult();
                        Log.d("uri string", downloadUrl.toString());
//                                uprofile.setImageURI(downloadUrl);
                        //Picasso.with(getContext()).load(downloadUrl).into(uprofile);
                        Glide.with(getApplicationContext())
                                .load(downloadUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(uprofile);
                    }
                    //process.dismiss();
                }

            });
        }

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                toolbar.setTitle("Profile");
                vibe.vibrate(100);
                break;
//            case R.id.nav_chat:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ChatFragment()).commit();
//                toolbar.setTitle("Chat");
//                vibe.vibrate(100);
//                break;
//            case R.id.nav_clean_up_location:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new LocationFragment()).commit();
//                toolbar.setTitle("Clean Up Map");
//                vibe.vibrate(100);
//                break;
            case R.id.nav_logout:
                vibe.vibrate(100);
                Logout_activity();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout_activity(){
        final CharSequence[] options = {"Yes", "No"};
        AlertDialog.Builder builder = new AlertDialog.Builder(com.example.androidgroupproject.MainAction.this);
        builder.setTitle("Do you want to LOG OUT ?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Yes")) {
                    mAuth.signOut();
                    startActivity(new Intent(com.example.androidgroupproject.MainAction.this,Login.class));
                    vibe.vibrate(100);
                    finish();
                } else if (options[item].equals("No")) {
                    vibe.vibrate(100);
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            Logout_activity();
        }
    }
}