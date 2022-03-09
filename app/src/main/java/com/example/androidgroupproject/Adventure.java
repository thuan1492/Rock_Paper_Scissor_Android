package com.example.androidgroupproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Adventure extends Fragment implements View.OnClickListener {
    ImageView b_rock, b_scissor, b_paper, cpu_choose, player_choose;
    String playerchoice, cpuchoice, result, sb;
    int playerwin, playerlose, pscore, bet;
    TextView  player_win, player_lose, score, betscore, ingamename;
    String getIngame;
    String getScore ;
    int rid, sid, pid;
    protected FirebaseUser user;
    protected FirebaseAuth mAuth;
    protected FirebaseFirestore userinfo;
    protected CollectionReference siteref;
    String email_id;
    int start = 0;
    Button quit;
    Random random;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_adventure, container, false);

        userinfo = FirebaseFirestore.getInstance();
        siteref = userinfo.collection("users");

        b_paper = (ImageView) view.findViewById(R.id.paper);
        b_scissor = (ImageView) view.findViewById(R.id.scissor);
        b_rock = (ImageView) view.findViewById(R.id.rock);

        cpu_choose = (ImageView) view.findViewById(R.id.cpu_choice);
        player_choose = (ImageView) view.findViewById(R.id.player_choice);

        player_win = (TextView) view.findViewById(R.id.player_win);
        player_lose = (TextView) view.findViewById(R.id.player_lose);
        score = (TextView) view.findViewById(R.id.score);
        betscore = (TextView) view.findViewById(R.id.betscore);
        ingamename = (TextView) view.findViewById(R.id.ingameName);

        quit = (Button) view.findViewById(R.id.quit);

        b_paper.setOnClickListener(this);
        b_scissor.setOnClickListener(this);
        b_rock.setOnClickListener(this);
        betscore.setOnClickListener(this);
        quit.setOnClickListener(this);


        bet = Integer.parseInt(betscore.getText().toString());
        playerwin = Integer.parseInt(player_win.getText().toString());
        playerlose = Integer.parseInt(player_lose.getText().toString());
        random = new Random();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        if (user != null) {
            email_id = user.getEmail();
            System.out.println(email_id);
            DocumentReference email = siteref.document(email_id);
            CollectionReference profile = email.collection("Profile");
            DocumentReference basicinfo = profile.document("Basic Information");

            basicinfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    getIngame = documentSnapshot.getString("Ingame");
                    getScore = documentSnapshot.getString("Score") ;
                    if(!getScore.equals(""))
                    {

                        score.setText(getScore);
                        pscore = Integer.parseInt(score.getText().toString());
                        ingamename.setText(getIngame);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            start = 1;
        }
        checkequipment();
        return view;
    }

    @Override
    public void onClick(View view) {
        if (bet != 0){
            if(view.getId() == R.id.paper){
                playerchoice = "paper";
                player_choose.setImageResource(pid);
                analysis();
            }
            else if(view.getId() == R.id.rock){
                playerchoice = "rock";
                player_choose.setImageResource(rid);
                analysis();
            }
            else if(view.getId() == R.id.scissor){
                playerchoice = "scissors";
                player_choose.setImageResource(sid);
                analysis();
            }

        }
        else if(view.getId() == R.id.quit){
            Intent intent = new Intent(getActivity(), MainAction.class);
            startActivity(intent);
        }
        else
        {
            choose_bet_score();
        }

    }

    private void choose_bet_score() {
        final CharSequence[] options = {"100","500", "1000", "5000","Half", "All"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Adventure.this.getContext());
        builder.setTitle("Choose your bet score!!!");
        builder.setItems(options, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals("100")) {
                            sb = String.valueOf(options[which]);
                        } else if (options[which].equals("500")) {
                            sb = String.valueOf(options[which]);
                        } else if (options[which].equals("1000")) {
                            sb = String.valueOf(options[which]);
                        } else if (options[which].equals("5000")) {
                            sb = String.valueOf(options[which]);
                        } else if (options[which].equals("Half")) {
                            sb = String.valueOf(pscore/2);
                        } else if (options[which].equals("All")) {
                            sb = String.valueOf(pscore);
                        }
                        calculate_bet(sb);
                    }
                });
        builder.show();
    }

    private void calculate_bet(String bets) {
        int s = Integer.parseInt(bets);
        if (s <= pscore){
            bet = s;
            betscore.setText(String.valueOf(bet));
            pscore = pscore - bet;
            score.setText(String.valueOf(pscore));
        }
        else {
            Toast.makeText(Adventure.this.getContext(), "Your score is not enough", Toast.LENGTH_SHORT).show();
        }
    }

    private void analysis() {
        int cpu = random.nextInt(3);
        if(cpu == 0){
            cpuchoice = "rock";
            cpu_choose.setImageResource(R.drawable.rock);
            cpu_choose.setRotation(180);
        }else if(cpu == 1){
            cpuchoice = "paper";
            cpu_choose.setImageResource(R.drawable.paper);
            cpu_choose.setRotation(180);
        }else if(cpu == 2){
            cpuchoice = "scissors";
            cpu_choose.setImageResource(R.drawable.scissors);
            cpu_choose.setRotation(180);
        }
        if(playerchoice.equals("rock") && cpuchoice.equals("paper")){
            result = "You lose";

        }
        if(playerchoice.equals("rock") && cpuchoice.equals("scissors")){
            result = "You win";
        }
        if(playerchoice.equals("rock") && cpuchoice.equals("rock")){
            result = "Draw";
        }
        if(playerchoice.equals("paper") && cpuchoice.equals("scissors")){
            result = "You lose";
        }
        if(playerchoice.equals("paper") && cpuchoice.equals("rock")){
            result = "You win";
        }
        if(playerchoice.equals("paper") && cpuchoice.equals("paper")){
            result = "Draw";
        }
        if(playerchoice.equals("scissors") && cpuchoice.equals("scissors")){
            result = "Draw";
        }
        if(playerchoice.equals("scissors") && cpuchoice.equals("rock")){
            result = "You lose";
        }
        if(playerchoice.equals("scissors") && cpuchoice.equals("paper")){
            result = "You win";
        }
        updateresult();
        Toast.makeText(Adventure.this.getContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void updateresult() {
        if(result.equals("You lose")){
            playerlose+=1;
            player_lose.setText(String.valueOf(playerlose));
            bet = 0;
            betscore.setText(String.valueOf(bet));
        }
        else if (result.equals("You win"))
        {
            playerwin+=1;
            player_win.setText(String.valueOf(playerwin));
            pscore = pscore + bet*2;
            score.setText(String.valueOf(pscore));
            bet = 0;
            betscore.setText(String.valueOf(bet));

        }
        else if (result.equals("Draw")){
            pscore = pscore + bet;
            score.setText(String.valueOf(pscore));
            bet = 0;
            betscore.setText(String.valueOf(bet));
        }
        updateScore();
    }

    private void updateScore() {
        user = mAuth.getCurrentUser();
        //database = FirebaseDatabase.getInstance();
        //myref = database.getReference();
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

    private void checkequipment() {

        // Add a new document with a generated ID
        siteref.document(email_id)
                .collection("Profile").document("CurrentItem")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot= task.getResult();
                    rid = Integer.parseInt(documentSnapshot.getString("rock"));
                    sid = Integer.parseInt(documentSnapshot.getString("scissors"));
                    pid = Integer.parseInt(documentSnapshot.getString("paper"));
                    b_paper.setImageResource(pid);
                    b_scissor.setImageResource(sid);
                    b_rock.setImageResource(rid);
                }

            }
        });
    }
}