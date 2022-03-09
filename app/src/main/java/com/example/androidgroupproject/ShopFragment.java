package com.example.androidgroupproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class ShopFragment extends Fragment implements View.OnClickListener {
    ImageView paper1, scissor1, rock1;
    TextView sprice1, rprice1, pprice1, score;
    protected FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;

    DocumentReference email;
    CollectionReference profile;

    String getScore, email_id, type;
    int start = 0;
    int pscore, imageid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");

        paper1 = (ImageView) view.findViewById(R.id.paper_1);
        scissor1 = (ImageView) view.findViewById(R.id.scissor_1);
        rock1 = (ImageView) view.findViewById(R.id.rock_1);

        pprice1 = (TextView) view.findViewById(R.id.pprice_1);
        rprice1 = (TextView) view.findViewById(R.id.rprice_1);
        sprice1 = (TextView) view.findViewById(R.id.sprice_1);
        score = (TextView) view.findViewById(R.id.score);

        paper1.setOnClickListener(this);
        scissor1.setOnClickListener(this);
        rock1.setOnClickListener(this);
//        pprice1.setOnClickListener(this);
//        rprice1.setOnClickListener(this);
//        sprice1.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (user != null) {
            email_id = user.getEmail();
            email = siteref.document(email_id);
            profile = email.collection("Profile");
            DocumentReference basicinfo = profile.document("Basic Information");

            basicinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    getScore = documentSnapshot.getString("Score") ;
                    if(!getScore.equals(""))
                    {

                        score.setText(getScore);
                        pscore = Integer.parseInt(score.getText().toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            start = 1;
        }
        checkItem();
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.paper_1){
            analyse_buy(view.getId());

        }
        else  if(view.getId() == R.id.scissor_1){
            analyse_buy(view.getId());

        }
        else  if(view.getId() == R.id.rock_1){
            analyse_buy(view.getId());

        }
    }
    private void analyse_buy(int id) {
        final CharSequence[] options = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopFragment.this.getContext());
        builder.setTitle("Do you want to buy this item?");
        builder.setCancelable(false)
                .setItems(options, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals("Yes")) {
                            calculate_budget(id);
                        } else if (options[which].equals("No")) {
                            dialog.cancel();
                        }

                    }
                });
        builder.show();
    }

    private void calculate_budget(int id) {

        if(id == R.id.paper_1){
            check_budget(Integer.parseInt(pprice1.getText().toString()), id);
        }
        else  if(id == R.id.scissor_1){
            check_budget(Integer.parseInt(sprice1.getText().toString()), id);
        }
        else  if(id == R.id.rock_1){
            check_budget(Integer.parseInt(rprice1.getText().toString()), id);
        }
    }

    private void check_budget(int price, int id) {
        if (price <= pscore)
        {
            pscore = pscore - price;
            score.setText(String.valueOf(pscore));
            if(id == R.id.paper_1){
                paper1.setBackgroundColor(0x61000000);
                pprice1.setBackgroundColor(0x61000000);
                paper1.setClickable(false);
                imageid = R.drawable.paper_s;
                //pprice1.setClickable(false);
                type = "paper";

            }
            else  if(id == R.id.scissor_1){
                scissor1.setBackgroundColor(0x61000000);
                sprice1.setBackgroundColor(0x61000000);
                scissor1.setClickable(false);
                imageid = R.drawable.edward_scissor;
                //sprice1.setClickable(false);
                type = "scissors";
            }
            else  if(id == R.id.rock_1){
                rock1.setBackgroundColor(0x61000000);
                rprice1.setBackgroundColor(0x61000000);
                rock1.setClickable(false);
                imageid = R.drawable.rock_r;
                //rprice1.setClickable(false);
                type = "rock";
            }
        }
        else
        {
            Toast.makeText(ShopFragment.this.getContext(), "Your gold is not enough!!", Toast.LENGTH_SHORT).show();
        }
        updateScore();
        collectItem(imageid, type);
    }
    private void updateScore() {
        user = mAuth.getCurrentUser();

        DocumentReference ref = userinfo.collection("users")
                .document(user.getEmail())
                .collection("Profile")
                .document("Basic Information");
//
        ref.update("Score",score.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("updated");
            }
        });
    }
    private void collectItem(int id, String itemname) {
        Map<String, Object> userinf = new HashMap<>();
        userinf.put("Type", itemname);
        userinf.put("status", false);
        // Add a new document with a generated ID
        userinfo.collection("users")
                .document(email_id)
                .collection("Item")
                .document(String.valueOf(id)).set(userinf)
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
    private void checkItem() {
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
                        if(id == R.drawable.paper_s){
                            paper1.setBackgroundColor(0x61000000);
                            pprice1.setBackgroundColor(0x61000000);
                            paper1.setClickable(false);
                            pprice1.setClickable(false);
                            type = "paper";

                        }
                        else  if(id == R.drawable.edward_scissor){
                            scissor1.setBackgroundColor(0x61000000);
                            sprice1.setBackgroundColor(0x61000000);
                            scissor1.setClickable(false);
                            sprice1.setClickable(false);
                            type = "scissors";
                        }
                        else  if(id == R.drawable.rock_r){
                            rock1.setBackgroundColor(0x61000000);
                            rprice1.setBackgroundColor(0x61000000);
                            rock1.setClickable(false);
                            rprice1.setClickable(false);
                            type = "rock";
                        }
                    }

                } else {

                }
            }
        });
    }

}
