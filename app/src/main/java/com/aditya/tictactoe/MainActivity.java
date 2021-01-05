package com.aditya.tictactoe;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Transfers the flow to the desired Game the User Selects.
        findViewById(R.id.singleplayer_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameLogic.class);
            intent.putExtra("Game", "SinglePlayer");
            startActivity(intent);
        });

        findViewById(R.id.local_mutiplayer_btn).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameLogic.class);
            intent.putExtra("Game", "MultiPlayer");
            startActivity(intent);
        });
    }
}