package hu.szakdolgozat.handballstatistics.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.NumberPicker;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.R;

public class StartedMatchActivity extends AppCompatActivity {
    DatabaseHelper db;
    NumberPicker minutesNumberPicker,secondNumberPicker;
    ConstraintLayout goalLayout;

    int playerId;
    String date, opponent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_match);
        playerId = getIntent().getIntExtra("id", -1);
        date = getIntent().getStringExtra("date");
        opponent = getIntent().getStringExtra("opponent");
    }
}