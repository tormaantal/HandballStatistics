package hu.szakdolgozat.handballstatistics.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class StatisticsActivity extends AppCompatActivity {

    PlayerServices playerServices;
    MatchServices matchServices;
    long id;
    ImageView statisticsBackIV;
    Button statisticsExportButton;
    TextView staticsDetailsTV, leftWingSaves, leftWingGoals, leftWingEfficiency,
            leftBackSaves, leftBackGoals, leftBackEfficiency,
            centralBackSaves, centralBackGoals, centralBackEfficiency,
            rightBackSaves, rightBackGoals, rightBackEfficiency,
            rightWingSaves, rightWingGoals, rightWingEfficiency,
            pivotSaves, pivotGoals, pivotEfficiency,
            breakInSaves, breakInGoals, breakInEfficiency,
            fastBreakSaves, fastBreakGoals, fastBreakEfficiency,
            sevenMetersSaves, sevenMetersGoals, sevenMetersEfficiency,
            summarySaves, summaryGoals, summaryEfficiency,
            longPass, twoMinutes, yellowCard, redCard, blueCard;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        init();
        loadStatistics();


        statisticsBackIV.setOnClickListener(view -> {
            finish();
        });
        statisticsExportButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view1 = getLayoutInflater().inflate(R.layout.export_dialog, null);
            builder.setView(view1);
            dialog = builder.create();
            dialog.show();
            dialog.findViewById(R.id.pdfExportButton).setOnClickListener(view2 -> {
                //Todo pdf készítése és mentése
                Toast.makeText(this, "PDF", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            dialog.findViewById(R.id.emailExportButton).setOnClickListener(view2 -> {
                Toast.makeText(this, "EMAIL", Toast.LENGTH_SHORT).show();
                //Todo pdf készítése és elküldése emailban
                dialog.dismiss();
            });
            dialog.findViewById(R.id.JsonExportButton).setOnClickListener(view2 -> {
                Toast.makeText(this, "JSON", Toast.LENGTH_SHORT).show();
                //Todo JSON fájl készítése és mentése
                dialog.dismiss();
            });
        });
    }

    private void init() {
        playerServices = new PlayerServices(this);
        matchServices= new MatchServices(this);
        statisticsBackIV = findViewById(R.id.statisticsBackIV);
        staticsDetailsTV = findViewById(R.id.staticsDetailsTV);
        statisticsExportButton = findViewById(R.id.statisticsExportButton);
    }

    private void loadStatistics() {
        String type = getIntent().getStringExtra("type");
        if (Objects.equals(type, "player")) {
            id = getIntent().getLongExtra("playerId", 0);
            staticsDetailsTV.setText(playerServices.findPlayerById(id).toString());
            //Todo játékos összes meccsének statisztikájának lekérése
            return;
        }
        if (Objects.equals(type, "match")){
            id = getIntent().getLongExtra("matchId", 0);
            Match match =matchServices.findMatchById(id);
            String text = match.getDate() +", "+
                    playerServices.findPlayerById(match.getPlayerId()).getName() + " vs "+
                    match.getOpponent();
            staticsDetailsTV.setText(text);
            //Todo adott mérkőzés statisztikájának lekérése
            return;
        }
    }

    private void sendEmail() {
        String[] to = {"t.anti94@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.not_found, Toast.LENGTH_SHORT).show();
        }
    }
}