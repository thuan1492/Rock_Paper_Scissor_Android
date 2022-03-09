package com.example.androidgroupproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryFragment extends Fragment implements View.OnClickListener {
    ImageView invpaper1, invscissor1, invrock1 ;
    TextView scisname, rockname, papername;
    protected FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;

    DocumentReference email;
    CollectionReference profile;

    String email_id, type;
    int start = 0;
    int def_skin, skin;
    boolean rockeq,scissoreq,papereq;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);


        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");

        //inventor id
        invpaper1 = (ImageView) view.findViewById(R.id.paper_1);
        invscissor1 = (ImageView) view.findViewById(R.id.scissor_1);
        invrock1 = (ImageView) view.findViewById(R.id.rock_1);

        papername = (TextView) view.findViewById(R.id.pprice_1);
        rockname = (TextView) view.findViewById(R.id.rprice_1);
        scisname = (TextView) view.findViewById(R.id.sprice_1);

        invpaper1.setOnClickListener(this);
        invscissor1.setOnClickListener(this);
        invrock1.setOnClickListener(this);

        invpaper1.setBackgroundColor(0x61000000);
        papername.setBackgroundColor(0x61000000);
        invpaper1.setClickable(false);



        invscissor1.setBackgroundColor(0x61000000);
        scisname.setBackgroundColor(0x61000000);
        invscissor1.setClickable(false);

        invrock1.setBackgroundColor(0x61000000);
        rockname.setBackgroundColor(0x61000000);
        invrock1.setClickable(false);




//        pprice1.setOnClickListener(this);
//        rprice1.setOnClickListener(this);
//        sprice1.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            email_id = user.getEmail();
            email = siteref.document(email_id);
            profile = email.collection("Profile");
            start = 1;
        }
        checkInventory();
        //checkallequipment();
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.paper_1){
            type = "paper";
            def_skin = R.drawable.paper;
            skin = R.drawable.paper_s;
            if(papereq == false){
                equipment(skin);
            }
            else equipment(def_skin);

        }
        else  if(view.getId() == R.id.scissor_1){

            type = "scissors";
            def_skin = R.drawable.scissors;
            skin = R.drawable.edward_scissor;
            if(scissoreq == false){
                equipment(skin);
            }
            else equipment(def_skin);
        }
        else  if(view.getId() == R.id.rock_1){

            type = "rock";
            def_skin = R.drawable.rock;
            skin = R.drawable.rock_r;
            if(rockeq == false){
                equipment(skin);
            }
            else equipment(def_skin);
        }
    }
    private void checkInventory() {

        // Add a new document with a generated ID
        siteref.document(email_id)
                .collection("Item")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int id = Integer.parseInt(document.getId());
                        boolean s = document.getBoolean("status");
                        if(id == R.drawable.paper_s){
                            if(s == false){
                                invpaper1.setBackgroundColor(0);
                                papername.setBackgroundColor(0);

                                type = "paper";
                            }else {
                                invpaper1.setBackgroundColor(Color.parseColor("#8BC34A"));
                                papername.setBackgroundColor(Color.parseColor("#8BC34A"));
                            }
                            papereq = s;
                            invpaper1.setClickable(true);

                        }
                        else  if(id == R.drawable.edward_scissor){
                            if(s == false){
                                invscissor1.setBackgroundColor(0);
                                scisname.setBackgroundColor(0);

                                type = "paper";
                            }else {
                                invscissor1.setBackgroundColor(Color.parseColor("#8BC34A"));
                                scisname.setBackgroundColor(Color.parseColor("#8BC34A"));
                            }
                            scissoreq = s;
                            invscissor1.setClickable(true);
                        }
                        else  if(id == R.drawable.rock_r){
                            invrock1.setBackgroundColor(0);
                            rockname.setBackgroundColor(0);
                            if(s == false){
                                invrock1.setBackgroundColor(0);
                                rockname.setBackgroundColor(0);

                                type = "paper";
                            }else {
                                invrock1.setBackgroundColor(Color.parseColor("#8BC34A"));
                                rockname.setBackgroundColor(Color.parseColor("#8BC34A"));
                            }
                            rockeq = s;
                            invrock1.setClickable(true);
                        }
                    }

                } else {

                }
            }
        });
    }

    private void equipment(int skin_id) {
        final CharSequence[] options = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(InventoryFragment.this.getContext());
        if(skin_id == skin)
        {
            builder.setTitle("Do you want to equip this item?");
            builder.setCancelable(false)
                    .setItems(options, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (options[which].equals("Yes")) {
                                update_equipment(type,skin_id, true);
                                checkequipment(skin_id);
                                dialog.cancel();
                            } else if (options[which].equals("No")) {
                                dialog.cancel();
                            }

                        }
                    });
            builder.show();
        }
        else {
            builder.setTitle("Do you want to unequip this item?");
            builder.setCancelable(false)
                    .setItems(options, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (options[which].equals("Yes")) {
                                update_equipment(type,skin_id, false);
                                unequipment(skin_id,type);
                                dialog.cancel();
                            } else if (options[which].equals("No")) {
                                dialog.cancel();
                            }

                        }
                    });
            builder.show();
        }


    }

    private void unequipment(int id, String t) {

        // Add a new document with a generated ID
        siteref.document(email_id)
                .collection("Profile").document("CurrentItem")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot= task.getResult();
                    int rid = Integer.parseInt(documentSnapshot.getString("rock"));
                    int sid = Integer.parseInt(documentSnapshot.getString("scissors"));
                    int pid = Integer.parseInt(documentSnapshot.getString("paper"));
                    if (rid == id){
                        invrock1.setBackgroundColor(0);
                        rockname.setBackgroundColor(0);

                    }
                    else if (sid == id){
                        invscissor1.setBackgroundColor(0);
                        scisname.setBackgroundColor(0);

                    }
                    else if (pid == id){
                        invpaper1.setBackgroundColor(0);
                        papername.setBackgroundColor(0);

                    }

                }

            }
        });
        Map<String, Object> equipment = new HashMap<>();
        if (t.equals("rock")){
            equipment.put("rock", String.valueOf(R.drawable.rock));
        }
        else if (t.equals("scissors"))
        {
            equipment.put("scissors", String.valueOf(R.drawable.scissors));
        }
        else if (t.equals("paper"))
        {
            equipment.put("paper", String.valueOf(R.drawable.paper));
        }

        // Add a new document with a generated ID
        siteref.document(email_id).collection("Profile").document("CurrentItem").update(equipment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void checkequipment(int id) {

        // Add a new document with a generated ID
        siteref.document(email_id)
                .collection("Profile").document("CurrentItem")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot= task.getResult();
                    int rid = Integer.parseInt(documentSnapshot.getString("rock"));
                    int sid = Integer.parseInt(documentSnapshot.getString("scissors"));
                    int pid = Integer.parseInt(documentSnapshot.getString("paper"));
                    if (rid == id){
                        invrock1.setBackgroundColor(Color.parseColor("#8BC34A"));
                        rockname.setBackgroundColor(Color.parseColor("#8BC34A"));

                    }
                    else if (sid == id){
                        invscissor1.setBackgroundColor(Color.parseColor("#8BC34A"));
                        scisname.setBackgroundColor(Color.parseColor("#8BC34A"));

                    }
                    else if (pid == id){
                        invpaper1.setBackgroundColor(Color.parseColor("#8BC34A"));
                        papername.setBackgroundColor(Color.parseColor("#8BC34A"));

                    }

                }

            }
        });
    }

    private void update_equipment(String t, int id, boolean s) {
        Map<String, Object> equipinf = new HashMap<>();
        equipinf.put(t, String.valueOf(id));
        DocumentReference ref = userinfo.collection("users")
                .document(email_id)
                .collection("Profile")
                .document("CurrentItem");
//
        ref.update(equipinf).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        Map<String, Object> status = new HashMap<>();
        status.put("status", s);
        Toast.makeText(getContext(),String.valueOf(id), Toast.LENGTH_SHORT).show();
        userinfo.collection("users")
                .document(email_id)
                .collection("Item")
                .document(String.valueOf(id))
                .update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

}

