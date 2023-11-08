package hu.szakdolgozat.handballstatistics.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.services.EventServices;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PdfServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class StatisticsActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_CODE = 1;
    PlayerServices playerServices;
    MatchServices matchServices;
    EventServices eventServices;
    PdfServices pdfServices;
    long playerId, matchId, save, goal;
    private final ActivityResultLauncher<Intent> startPickPdfActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendEmail(uri);
                        }
                    }
                } else {
                    Toast.makeText(StatisticsActivity.this, "Nincs kiválasztva fájl", Toast.LENGTH_SHORT).show();
                }
            }
    );
    double efficiency;
    String methodType;
    Button expJson, expEmail, expPdf;
    ImageView statisticsBackIV;
    NumberFormat formatter;
    TextView tvStaticsDetails, tvLeftWingSaves, tvLeftWingGoals, tvLeftWingEfficiency,
            tvLeftBackSaves, tvLeftBackGoals, tvLeftBackEfficiency,
            tvCentralBackSaves, tvCentralBackGoals, tvCentralBackEfficiency,
            tvRightBackSaves, tvRightBackGoals, tvRightBackEfficiency,
            tvRightWingSaves, tvRightWingGoals, tvRightWingEfficiency,
            tvPivotSaves, tvPivotGoals, tvPivotEfficiency,
            tvBreakInSaves, tvBreakInGoals, tvBreakInEfficiency,
            tvFastBreakSaves, tvFastBreakGoals, tvFastBreakEfficiency,
            tvSevenMetersSaves, tvSevenMetersGoals, tvSevenMetersEfficiency,
            tvSummarySaves, tvSummaryGoals, tvSummaryEfficiency,
            tvTwoMinutes, tvYellowCard, tvRedCard, tvBlueCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        init();
        if (Objects.equals(methodType, "player")) {
            loadPlayerStatistics(playerId);
            exportPlayer();
        }
        if (Objects.equals(methodType, "match")) {
            loadMatchStatistics(matchId);
            exportMatch();
        }

        statisticsBackIV.setOnClickListener(view ->
                finish()
        );
    }

    private void init() {
        playerServices = new PlayerServices(this);
        matchServices = new MatchServices(this);
        eventServices = new EventServices(this);
        pdfServices = new PdfServices(this);
        formatter = new DecimalFormat("#0.0");
        methodType = getIntent().getStringExtra("methodType");
        playerId = getIntent().getLongExtra("playerId", -1);
        matchId = getIntent().getLongExtra("matchId", -1);
        expEmail = findViewById(R.id.expEmail);
        expJson = findViewById(R.id.expJson);
        expPdf = findViewById(R.id.expPdf);
        statisticsBackIV = findViewById(R.id.statisticsBackIV);
        tvStaticsDetails = findViewById(R.id.staticsDetailsTV);
        tvLeftWingGoals = findViewById(R.id.leftWingGoals);
        tvLeftWingSaves = findViewById(R.id.leftWingSaves);
        tvLeftWingEfficiency = findViewById(R.id.leftWingEfficiency);
        tvLeftBackGoals = findViewById(R.id.leftBackGoals);
        tvLeftBackSaves = findViewById(R.id.leftBackSaves);
        tvLeftBackEfficiency = findViewById(R.id.leftBackEfficiency);
        tvCentralBackGoals = findViewById(R.id.centralBackGoals);
        tvCentralBackSaves = findViewById(R.id.centralBackSaves);
        tvCentralBackEfficiency = findViewById(R.id.centralBackEfficiency);
        tvRightBackGoals = findViewById(R.id.rightBackGoals);
        tvRightBackSaves = findViewById(R.id.rightBackSaves);
        tvRightBackEfficiency = findViewById(R.id.rightBackEfficiency);
        tvRightWingGoals = findViewById(R.id.rightWingGoals);
        tvRightWingSaves = findViewById(R.id.rightWingSaves);
        tvRightWingEfficiency = findViewById(R.id.rightWingEfficiency);
        tvPivotGoals = findViewById(R.id.pivotGoals);
        tvPivotSaves = findViewById(R.id.pivotSaves);
        tvPivotEfficiency = findViewById(R.id.pivotEfficiency);
        tvBreakInGoals = findViewById(R.id.breakInGoals);
        tvBreakInSaves = findViewById(R.id.breakInSaves);
        tvBreakInEfficiency = findViewById(R.id.breakInEfficiency);
        tvFastBreakGoals = findViewById(R.id.fastBreakGoals);
        tvFastBreakSaves = findViewById(R.id.fastBreakSaves);
        tvFastBreakEfficiency = findViewById(R.id.fastBreakEfficiency);
        tvSevenMetersGoals = findViewById(R.id.sevenMetersGoals);
        tvSevenMetersSaves = findViewById(R.id.sevenMetersSaves);
        tvSevenMetersEfficiency = findViewById(R.id.sevenMetersEfficiency);
        tvSummaryGoals = findViewById(R.id.summaryGoals);
        tvSummarySaves = findViewById(R.id.summarySaves);
        tvSummaryEfficiency = findViewById(R.id.summaryEfficiency);
        tvTwoMinutes = findViewById(R.id.twoMinutes);
        tvYellowCard = findViewById(R.id.yellowCard);
        tvRedCard = findViewById(R.id.redCard);
        tvBlueCard = findViewById(R.id.blueCard);
    }

    private void exportPlayer() {
        expPdf.setOnClickListener(view ->
                pdfServices.generatePlayerPdf(playerId)
        );
        expEmail.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            } else {
                pdfPicker();
            }
        });
    }

    private void exportMatch() {
        expJson.setOnClickListener(view -> {
        });
        expPdf.setOnClickListener(view ->
                pdfServices.generateMatchPdf(playerId, matchId)
        );
        expEmail.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission
                    (this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            } else {
                pdfPicker();
            }
        });

    }

    private void pdfPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        startPickPdfActivity.launch(intent);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pdfPicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadPlayerStatistics(long playerId) {
        tvStaticsDetails.setText(playerServices.findPlayerById(playerId).toString());
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.LEFTWING);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.LEFTWING);
        efficiency = (double) save / (goal + save) * 100;
        tvLeftWingSaves.setText(String.valueOf(save));
        tvLeftWingGoals.setText(String.valueOf(goal));
        tvLeftWingEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.LEFTBACK);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.LEFTBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvLeftBackSaves.setText(String.valueOf(save));
        tvLeftBackGoals.setText(String.valueOf(goal));
        tvLeftBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.CENTERBACK);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.CENTERBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvCentralBackSaves.setText(String.valueOf(save));
        tvCentralBackGoals.setText(String.valueOf(goal));
        tvCentralBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.RIGHTBACK);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.RIGHTBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvRightBackSaves.setText(String.valueOf(save));
        tvRightBackGoals.setText(String.valueOf(goal));
        tvRightBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.RIGHTWING);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.RIGHTWING);
        efficiency = (double) save / (goal + save) * 100;
        tvRightWingSaves.setText(String.valueOf(save));
        tvRightWingGoals.setText(String.valueOf(goal));
        tvRightWingEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.PIVOT);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.PIVOT);
        efficiency = (double) save / (goal + save) * 100;
        tvPivotSaves.setText(String.valueOf(save));
        tvPivotGoals.setText(String.valueOf(goal));
        tvPivotEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.FASTBREAK);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.FASTBREAK);
        efficiency = (double) save / (goal + save) * 100;
        tvFastBreakSaves.setText(String.valueOf(save));
        tvFastBreakGoals.setText(String.valueOf(goal));
        tvFastBreakEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.BREAKIN);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.BREAKIN);
        efficiency = (double) save / (goal + save) * 100;
        tvBreakInSaves.setText(String.valueOf(save));
        tvBreakInGoals.setText(String.valueOf(goal));
        tvBreakInEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayerByType(playerId, EventType.SEVENMETERS);
        goal = playerServices.findAllGoalByPlayerByType(playerId, EventType.SEVENMETERS);
        efficiency = (double) save / (goal + save) * 100;
        tvSevenMetersSaves.setText(String.valueOf(save));
        tvSevenMetersGoals.setText(String.valueOf(goal));
        tvSevenMetersEfficiency.setText(formatter.format(efficiency) + "%");
        save = playerServices.findAllSaveByPlayer(playerId);
        goal = playerServices.findAllGoalByPlayer(playerId);
        efficiency = (double) save / (goal + save) * 100;
        tvSummarySaves.setText(String.valueOf(save));
        tvSummaryGoals.setText(String.valueOf(goal));
        tvSummaryEfficiency.setText(formatter.format(efficiency) + "%");
        tvYellowCard.setText(playerServices.findAllYellowCardByPlayer(playerId) + " db");
        tvTwoMinutes.setText(playerServices.findAllTwoMinutesByPlayer(playerId) + " db");
        tvRedCard.setText(playerServices.findAllRedCardByPlayer(playerId) + " db");
        tvBlueCard.setText(playerServices.findAllBlueCardByPlayer(playerId) + " db");
    }

    @SuppressLint("SetTextI18n")
    private void loadMatchStatistics(long matchId) {
        Match m = matchServices.findMatchById(matchId);
        tvStaticsDetails.setText(m.getDate() + " " + m.getOpponent() + " vs " + playerServices.findPlayerById(m.getPlayerId()));
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.LEFTWING);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.LEFTWING);
        efficiency = (double) save / (goal + save) * 100;
        tvLeftWingSaves.setText(String.valueOf(save));
        tvLeftWingGoals.setText(String.valueOf(goal));
        tvLeftWingEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.LEFTBACK);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.LEFTBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvLeftBackSaves.setText(String.valueOf(save));
        tvLeftBackGoals.setText(String.valueOf(goal));
        tvLeftBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.CENTERBACK);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.CENTERBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvCentralBackSaves.setText(String.valueOf(save));
        tvCentralBackGoals.setText(String.valueOf(goal));
        tvCentralBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.RIGHTBACK);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.RIGHTBACK);
        efficiency = (double) save / (goal + save) * 100;
        tvRightBackSaves.setText(String.valueOf(save));
        tvRightBackGoals.setText(String.valueOf(goal));
        tvRightBackEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.RIGHTWING);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.RIGHTWING);
        efficiency = (double) save / (goal + save) * 100;
        tvRightWingSaves.setText(String.valueOf(save));
        tvRightWingGoals.setText(String.valueOf(goal));
        tvRightWingEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.PIVOT);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.PIVOT);
        efficiency = (double) save / (goal + save) * 100;
        tvPivotSaves.setText(String.valueOf(save));
        tvPivotGoals.setText(String.valueOf(goal));
        tvPivotEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.FASTBREAK);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.FASTBREAK);
        efficiency = (double) save / (goal + save) * 100;
        tvFastBreakSaves.setText(String.valueOf(save));
        tvFastBreakGoals.setText(String.valueOf(goal));
        tvFastBreakEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.BREAKIN);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.BREAKIN);
        efficiency = (double) save / (goal + save) * 100;
        tvBreakInSaves.setText(String.valueOf(save));
        tvBreakInGoals.setText(String.valueOf(goal));
        tvBreakInEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatchByType(matchId, EventType.SEVENMETERS);
        goal = matchServices.findAllGoalByMatchByType(matchId, EventType.SEVENMETERS);
        efficiency = (double) save / (goal + save) * 100;
        tvSevenMetersSaves.setText(String.valueOf(save));
        tvSevenMetersGoals.setText(String.valueOf(goal));
        tvSevenMetersEfficiency.setText(formatter.format(efficiency) + "%");
        save = matchServices.findAllSaveByMatch(matchId);
        goal = matchServices.findAllGoalByMatch(matchId);
        efficiency = (double) save / (goal + save) * 100;
        tvSummarySaves.setText(String.valueOf(save));
        tvSummaryGoals.setText(String.valueOf(goal));
        tvSummaryEfficiency.setText(formatter.format(efficiency) + "%");
        tvYellowCard.setText(matchServices.findAllYellowCardByMatch(matchId) + " db");
        tvTwoMinutes.setText(matchServices.findAllTwoMinutesByMatch(matchId) + " db");
        tvRedCard.setText(matchServices.findAllRedCardByMatch(matchId) + " db");
        tvBlueCard.setText(matchServices.findAllBlueCardByMatch(matchId) + " db");
    }

    private void sendEmail(Uri uri) {
        String[] to = {"t.anti94@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, playerServices.findPlayerById(playerId).toString());
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(emailIntent);
    }

}