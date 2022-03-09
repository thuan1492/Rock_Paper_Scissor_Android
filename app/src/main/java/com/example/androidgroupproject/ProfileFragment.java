package com.example.androidgroupproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Vibrator vibe ;
    protected FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;
    private ProgressDialog process;
    EditText score, phone, ingame, birth, fname;
    TextView save;
    ImageView uprofile;
    Button changepassword;
    String getname ;
    String getScore ;
    String getphone;
    String getIngame;
    String getbirth;
    int start = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        process = new ProgressDialog(getContext());
        process.setTitle("Loading...");
        process.setCancelable(false);
        process.show();

        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");
        score = (EditText) view.findViewById(R.id.score);
        phone = (EditText) view.findViewById(R.id.phnumber);
        ingame = (EditText) view.findViewById(R.id.ingameName);
        birth = (EditText) view.findViewById(R.id.birthday);
        fname = (EditText) view.findViewById(R.id.fullname);
        save = (TextView) view.findViewById(R.id.save_edit);
        uprofile = (ImageView) view.findViewById(R.id.pic_info);
        changepassword = (Button) view.findViewById(R.id.change_pass);
        save.setOnClickListener(this);
        uprofile.setOnClickListener(this);
        changepassword.setOnClickListener(this);
        ingame.setOnClickListener(this);
        birth.setOnClickListener(this);

        fname.setEnabled(false);
        score.setEnabled(false);
        birth.setEnabled(false);
        phone.setEnabled(false);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (user != null) {
            String uphotos = "Users";
            String profile_fol = "Profiles";
            String email_id = user.getEmail();
            DocumentReference email = siteref.document(email_id);
            CollectionReference profile = email.collection("Profile");
            DocumentReference basicinfo = profile.document("Basic Information");

            basicinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    getname = documentSnapshot.getString("Name");
                    getScore = documentSnapshot.getString("Score") ;
                    getphone = documentSnapshot.getString("Phone");
                    getIngame = documentSnapshot.getString("Ingame");
                    getbirth = documentSnapshot.getString("Birthday");
                    if(!getname.equals(""))
                    {
                        fname.setText(getname);
                        score.setText(getScore);
                        phone.setText(getphone);
                        ingame.setText(getIngame);
                        birth.setText(getbirth);
//                        score.addTextChangedListener(textWatcher);
//                        phone.addTextChangedListener(textWatcher);
                        ingame.addTextChangedListener(textWatcher);
//                        birth.addTextChangedListener(textWatcher);
//                        fname.addTextChangedListener(textWatcher);
                    }

                    StorageReference profilestorage = FirebaseStorage.getInstance().getReference().child(uphotos+"/"+email_id+"/"+ profile_fol +"/"+ getname +".jpg");
                    profilestorage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                System.out.println("hello");
                                Uri downloadUrl = task.getResult();
                                Log.d("uri string", downloadUrl.toString());
//                              Load the image using Glide
                                Glide.with(getContext())
                                        .load(downloadUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(uprofile);
                            }
                            process.dismiss();
                        }

                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            start = 1;
        }

        return view;
    }


    private TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            //Toast.makeText(getContext(), "EditText changed",Toast.LENGTH_SHORT).show();
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //save.setVisibility(View.INVISIBLE);
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Toast.makeText(getContext(), "changing",Toast.LENGTH_SHORT).show();
            save.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_edit:
                vibe.vibrate(100);
                save.setVisibility(View.INVISIBLE);
                saveEdit();
                break;
            case R.id.change_pass:
                vibe.vibrate(100);
                System.out.println("change pass");
                startActivity(new Intent(getContext(),ChangePass.class));
                vibe.vibrate(100);
                break;
            case R.id.birthday:
                vibe.vibrate(100);
                break;
            case R.id.ingameName:
                vibe.vibrate(100);
                break;

        }
    }

    private void saveEdit() {
        user = mAuth.getCurrentUser();
        //database = FirebaseDatabase.getInstance();
        //myref = database.getReference();
        DocumentReference ref = userinfo.collection("users")
                .document(user.getEmail())
                .collection("Profile")
                .document("Basic Information");
//        System.out.println(ingame.getText());
        ref.update("Ingame",ingame.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("updated");
            }
        });
    }

}

