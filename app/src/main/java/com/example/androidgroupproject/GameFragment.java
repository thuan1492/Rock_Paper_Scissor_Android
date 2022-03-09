package com.example.androidgroupproject;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFragment extends Fragment implements View.OnClickListener{
    Button pvp, adven;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);


        pvp = (Button) view.findViewById(R.id.pvp);
        adven = (Button) view.findViewById(R.id.adven);


        pvp.setOnClickListener(this);
        adven.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.pvp){

        }
        else if(view.getId() == R.id.adven){
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Adventure()).commit();
        }
    }


}

