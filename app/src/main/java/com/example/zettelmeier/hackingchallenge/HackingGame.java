package com.example.zettelmeier.hackingchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class HackingGame extends AppCompatActivity {

        private GameView gameView;
        private Button btn;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            final GameView gameView = new GameView(this);



            setContentView(gameView);


        }

}
