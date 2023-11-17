package hu.szakdolgozat.handballstatistics.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.services.ImpExpService;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class StatisticsActivity extends AppCompatActivity {
    private PlayerServices playerServices;
    private MatchServices matchServices;
    private ImpExpService impExpService;
    private long playerId, matchId, save, goal;
    private int exportType;
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (checkPermission()) {
                            switch (exportType) {
                                case 1:
                                    impExpService.generatePlayerPdf();
                                    break;
                                case 2:
                                    impExpService.generatePlayerJson();
                                    break;
                                case 3:
                                    impExpService.generateMatchPdf();
                                    break;
                                case 4:
                                    impExpService.generateMatchJson();
                                    break;
                            }
                            Log.d("TAG", "onActivityResult: Manage External Storage Permissions Granted");
                        } else {
                            Toast.makeText(StatisticsActivity.this, "Hozzáférés megtagadva", Toast.LENGTH_SHORT).show();
                        }
                    });
    private double efficiency;
    private String methodType;
    private Button expJson, expEmail, expPdf;
    private ImageView statisticsBackIV;
    private NumberFormat formatter;
    private TextView tvStaticsDetails, tvLeftWingSaves, tvLeftWingGoals, tvLeftWingEfficiency,
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
        Log.e("TAG", Environment.isExternalStorageManager() + "");
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
        methodType = getIntent().getStringExtra("methodType");
        playerId = getIntent().getLongExtra("playerId", -1);
        matchId = getIntent().getLongExtra("matchId", -1);
        playerServices = new PlayerServices(this);
        matchServices = new MatchServices(this);
        impExpService = new ImpExpService(this, playerId, matchId);
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
        formatter = new DecimalFormat("#0.0");
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

    private void exportPlayer() {
        expPdf.setOnClickListener(v -> {
            if (!checkPermission()) {
                exportType = 1;
                requestPermission();
            } else {
                impExpService.generatePlayerPdf();
            }
        });
        expJson.setOnClickListener(v -> {
            if (!checkPermission()) {
                exportType = 2;
                requestPermission();
            } else {
                impExpService.generatePlayerJson();
            }
        });
        expEmail.setOnClickListener(v -> {
            if (!checkPermission()) {
                requestPermission();
            } else {
                File file = impExpService.generatePlayerPdfToEmail();
                if (file != null) {
                    sendPdfInEmail(file);
                } else {
                    Toast.makeText(this, "Hiba a fájl csatolásánál!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void exportMatch() {
        expPdf.setOnClickListener(v -> {
            if (!checkPermission()) {
                exportType = 3;
                requestPermission();
            } else {
                impExpService.generateMatchPdf();
            }
        });
        expJson.setOnClickListener(v -> {
            if (!checkPermission()) {
                exportType = 4;
                requestPermission();
            } else {
                impExpService.generateMatchJson();
            }
        });
        expEmail.setOnClickListener(v -> {
            if (!checkPermission()) {
                requestPermission();
            } else {
                File file = impExpService.generateMatchPdfToEmail();
                if (file != null) {
                    sendPdfInEmail(file);
                } else {
                    Toast.makeText(this, "Hiba a fájl csatolásánál!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPdfInEmail(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".file.provider", file);
        String[] to = {"t.anti94@gmail.com"};
        Intent intent = new ShareCompat.IntentBuilder(this)
                .setStream(uri)
                .getIntent()
                .setAction(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, to)
                .putExtra(Intent.EXTRA_SUBJECT, playerServices.findPlayerById(playerId).toString())
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void requestPermission() {

        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            storageActivityResultLauncher.launch(intent);
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            storageActivityResultLauncher.launch(intent);
        }
    }

    private boolean checkPermission() {
        return Environment.isExternalStorageManager();
    }
}