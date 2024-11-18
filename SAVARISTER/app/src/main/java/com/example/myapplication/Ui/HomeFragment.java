package com.example.myapplication.Ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.AdminHomeActivity;
import com.example.myapplication.BurgerActivity;
import com.example.myapplication.PizzaActivity;
import com.example.myapplication.R;


public class HomeFragment extends Fragment {

    private Button button8;
    private Button button9;

    private Button buttonadmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons
        button8 = view.findViewById(R.id.button8);
        button9 = view.findViewById(R.id.button9);
        buttonadmin = view.findViewById(R.id.button15);


        // Set click listeners for the buttons
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to burgers activity
                Intent intent = new Intent(getActivity(), BurgerActivity.class);
                startActivity(intent);
            }
        });

        buttonadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to burgers activity
                Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                startActivity(intent);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to pizza activity
                Intent intent = new Intent(getActivity(), PizzaActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
