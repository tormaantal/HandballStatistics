package hu.szakdolgozat.handballstatistics.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.models.Player;
import hu.szakdolgozat.handballstatistics.services.EventServices;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class StatisticsActivity extends AppCompatActivity {
    private static final EventType[] eventTypes = {
            EventType.LEFTWING, EventType.LEFTBACK, EventType.CENTERBACK,
            EventType.RIGHTBACK, EventType.RIGHTWING, EventType.PIVOT,
            EventType.FASTBREAK, EventType.BREAKIN, EventType.SEVENMETERS
    };
    private static final int PERMISSION_REQUEST_CODE = 1;
    private PlayerServices playerServices;
    private MatchServices matchServices;
    private EventServices eventServices;
    private long playerId, matchId, save, goal;
    private int exportType;
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
            if (checkPermission()) {
                exportType = 1;
                Log.e("TAG:", "PlayerExpPDFClick nincs engedély");
                requestPermission();
            } else {
                Log.e("TAG:", "PlayerExpPDFClick van engedély");
                generatePlayerPdf();
            }
        });
        expJson.setOnClickListener(v -> {
            if (checkPermission()) {
                exportType = 2;
                Log.e("TAG:", "PlayerExpJSONClick nincs engedély");
                requestPermission();
            } else {
                Log.e("TAG:", "PlayerExpJSONClick van engedély");
                generatePlayerJson();
            }
        });
        expEmail.setOnClickListener(v -> {
            if (checkPermission()) {
                Log.e("TAG:", "PlayerExpEmailClick nincs engedély");
                requestPermission();
            } else {
                Log.e("TAG:", "PlayerExpEmailClick van engedély");
                generatePlayerPdfToEmail();
            }
        });
    }

    private void exportMatch() {
        expPdf.setOnClickListener(v -> {
            if (checkPermission()) {
                exportType = 3;
                Toast.makeText(this, "NINCS ÍRÁS ENGEDÉLY!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else {
                generateMatchPdf();
            }
        });
        expJson.setOnClickListener(v -> {
            if (checkPermission()) {
                exportType = 4;
                Toast.makeText(this, "NINCS ÍRÁS ENGEDÉLY!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else {
                generateMatchJson();
            }
        });
        expEmail.setOnClickListener(v -> {
            if (checkPermission()) {
                Toast.makeText(this, "NINCS ÍRÁS ENGEDÉLY!", Toast.LENGTH_SHORT).show();
                requestPermission();
            } else {
                generateMatchPdfToEmail();
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == RESULT_OK) {
                Log.e("TAG:", "onRequestPermissionsResult Oké");
                Toast.makeText(this, "Hozzáférés engedélyezve!", Toast.LENGTH_SHORT).show();
                switch (exportType) {
                    case 1:
                        generatePlayerPdf();
                        break;
                    case 2:
                        generatePlayerJson();
                        break;
                    case 3:
                        generateMatchPdf();
                        break;
                    case 4:
                        generateMatchJson();
                        break;
                }
            } else {
                Log.e("TAG:", "onRequestPermissionsResult WRITE Nem oké");
                Toast.makeText(this, "Írás megtagadva!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void generatePlayerJson() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(this, "Nincs adat!", Toast.LENGTH_SHORT).show();
        } else {
            Player player = playerServices.findPlayerById(playerId);
            ArrayList<Event> events = new ArrayList<>();
            matches.forEach(m -> events.addAll(eventServices.findAllEventByMatchId(m.getMatchId())));
            String fileName = player.getFileName() + "_ALL_" +
                    System.currentTimeMillis() + ".json";
            Gson gson = new Gson();
            String jsonPlayer = gson.toJson(player);
            String jsonMatches = gson.toJson(matches);
            String jsonEvents = gson.toJson(events);
            JsonObject combinedJson = new JsonObject();
            combinedJson.add("Player", JsonParser.parseString(jsonPlayer));
            combinedJson.add("Matches", JsonParser.parseString(jsonMatches));
            combinedJson.add("Events", JsonParser.parseString(jsonEvents));
            File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(externalDir, fileName);
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(combinedJson, writer);
                Log.e("TAG", file.getAbsolutePath());
                Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("TAG", e.toString());
            }
        }
    }

    private void generateMatchJson() {
        Player player = playerServices.findPlayerById(playerId);
        Match matches = matchServices.findMatchById(matchId);
        ArrayList<Event> events = eventServices.findAllEventByMatchId(matchId);
        String fileName = player.getFileName() + matches.getDate() + "_" + matches.getOpponent() + "_" +
                System.currentTimeMillis() + ".json";
        Gson gson = new Gson();
        String jsonPlayer = gson.toJson(player);
        String jsonMatches = gson.toJson(matches);
        String jsonEvents = gson.toJson(events);
        JsonObject combinedJson = new JsonObject();
        combinedJson.add("Player", JsonParser.parseString(jsonPlayer));
        combinedJson.add("Matches", JsonParser.parseString(jsonMatches));
        combinedJson.add("Events", JsonParser.parseString(jsonEvents));
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(externalDir, fileName);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(combinedJson, writer);
            Log.e("TAG", file.getAbsolutePath());
            Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("TAG", e.toString());
        }
    }

    private void generatePlayerPdf() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(this, "Nincs adat! Előbb rögzítsen mérkőzést az adott játékoshoz!", Toast.LENGTH_SHORT).show();
        } else {
            Player player = playerServices.findPlayerById(playerId);
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            String fileName = player.getFileName() + "_ALL_" + System.currentTimeMillis() + ".pdf";
            File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(externalDir, fileName);
            drawTitle(page, player + " összesített adatok");
            drawTable(page, setDataPlayer(playerId));
            pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
                matches.forEach(match -> {
                    String t = player + " vs " + match.getOpponent() + " (" + match.getDate() + ")";
                    PdfDocument.Page p = pdfDocument.startPage(pageInfo);
                    drawTitle(p, t);
                    drawTable(p, setDataMatch(match.getMatchId()));
                    pdfDocument.finishPage(p);
                    try {
                        pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
                    } catch (IOException e) {
                        Log.e("TAG", e.toString());
                    }
                });
                pdfDocument.close();
                Log.e("TAG", file.getAbsolutePath());
                Toast.makeText(this, getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("TAG: exception catch:asd ", e.toString());
            }
        }
    }

    private void generateMatchPdf() {
        Player player = playerServices.findPlayerById(playerId);
        Match match = matchServices.findMatchById(matchId);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        String fileName = player.getFileName() + "_vs_" +
                match.getOpponent().trim() + "_" +
                System.currentTimeMillis() + ".pdf";
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(externalDir, fileName);
        try {
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            drawTitle(page, player + " vs " + match.getOpponent() + "(" + match.getDate() + ")");
            drawTable(page, setDataMatch(match.getMatchId()));
            pdfDocument.finishPage(page);
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
            Log.e("TAG", file.getAbsolutePath());
            Toast.makeText(this, getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private void generatePlayerPdfToEmail() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(this, "Nincs adat! Előbb rögzítsen mérkőzést az adott játékoshoz!", Toast.LENGTH_SHORT).show();
        } else {
            Player player = playerServices.findPlayerById(playerId);
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            String fileName = player.getFileName() + "_ALL_" + System.currentTimeMillis() + ".pdf";
            File externalDir = new File(getFilesDir().getAbsolutePath());
            File file = new File(externalDir, fileName);
            drawTitle(page, player + " összesített adatok");
            drawTable(page, setDataPlayer(playerId));
            pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
                matches.forEach(match -> {
                    String t = player + " vs " + match.getOpponent() + " (" + match.getDate() + ")";
                    PdfDocument.Page p = pdfDocument.startPage(pageInfo);
                    drawTitle(p, t);
                    drawTable(p, setDataMatch(match.getMatchId()));
                    pdfDocument.finishPage(p);
                    try {
                        pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
                    } catch (IOException e) {
                        Log.e("TAG", e.toString());
                    }
                });
                pdfDocument.close();
                Toast.makeText(this, getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("TAG: exception catch:asd ", e.toString());
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".file.provider", file);
            String[] to = {"t.anti94@gmail.com"};
            Intent intent = ShareCompat.IntentBuilder.from(this)
                    .setStream(uri)
                    .getIntent()
                    .setAction(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("mailto:"))
                    .putExtra(Intent.EXTRA_EMAIL, to)
                    .putExtra("fileName", fileName)
                    .putExtra(Intent.EXTRA_SUBJECT, playerServices.findPlayerById(playerId).toString())
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }

    private void generateMatchPdfToEmail() {
        Player player = playerServices.findPlayerById(playerId);
        Match match = matchServices.findMatchById(matchId);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        String fileName = player.getFileName() + "_vs_" +
                match.getOpponent().trim() + "_" +
                System.currentTimeMillis() + ".pdf";
        File externalDir = new File(getFilesDir().getAbsolutePath());
        File file = new File(externalDir, fileName);
        try {
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            drawTitle(page, player + " vs " + match.getOpponent() + "(" + match.getDate() + ")");
            drawTable(page, setDataMatch(match.getMatchId()));
            pdfDocument.finishPage(page);
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
            Log.e("TAG", file.getAbsolutePath());
            Toast.makeText(this, getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".file.provider", file);
        String[] to = {"t.anti94@gmail.com"};
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setStream(uri)
                .getIntent()
                .setAction(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, to)
                .putExtra("fileName", fileName)
                .putExtra(Intent.EXTRA_SUBJECT, playerServices.findPlayerById(playerId).toString())
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void drawTitle(PdfDocument.Page page, String title) {
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);
        float pageWidth = page.getInfo().getPageWidth();
        float x = pageWidth / 2 - titlePaint.measureText(title) / 2;
        float y = 30;
        page.getCanvas().drawText(title, x, y, titlePaint);
    }

    private void drawTable(PdfDocument.Page page, String[][] tableData) {
        int tableRows = 13;
        int tableCols = 4;
        float cellWidth = page.getInfo().getPageWidth() / 2f / tableCols;
        float cellHeight = page.getInfo().getPageHeight() / 2f / tableRows;
        for (int row = 0; row < tableRows; row++) {
            for (int col = 0; col < tableCols; col++) {
                float left = col * cellWidth + page.getInfo().getPageWidth() / 2f - 2 * cellWidth;
                float top = row * cellHeight + 55;
                float right = left + cellWidth;
                float bottom = top + cellHeight;
                Paint cellPaint = new Paint();
                cellPaint.setColor(Color.BLACK);
                cellPaint.setStyle(Paint.Style.STROKE);
                cellPaint.setStrokeWidth(1);
                page.getCanvas().drawRect(left, top, right, bottom, cellPaint);
                String text = tableData[row][col];
                Paint textPaint = new Paint();
                float x = left + (cellWidth - textPaint.measureText(text)) / 2;
                float y = top + cellHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2);
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(12);
                page.getCanvas().drawText(text, x, y, textPaint);
            }
        }
    }

    private String[][] setDataPlayer(long playerId) {
        NumberFormat formatter = new DecimalFormat("#0.0");
        long save, goal;
        double efficiency;
        String[][] rv = new String[][]{
                {getString(R.string.position), getString(R.string.save), getString(R.string.goal), getString(R.string.summary)},
                {getString(R.string.leftWing), "", "", ""},
                {getString(R.string.leftBack), "", "", ""},
                {getString(R.string.centralBack), "", "", ""},
                {getString(R.string.rightBack), "", "", ""},
                {getString(R.string.rightWing), "", "", ""},
                {getString(R.string.pivot), "", "", ""},
                {getString(R.string.breakIn), "", "", ""},
                {getString(R.string.fastBreak), "", "", ""},
                {getString(R.string.sevenMeters), "", "", ""},
                {getString(R.string.summary), "", "", ""},
                {getString(R.string.yellowCard), getString(R.string.twoMinutes), getString(R.string.redCard), getString(R.string.blueCard),},
                {"", "", "", ""}
        };
        for (int i = 0; i < eventTypes.length; i++) {
            save = playerServices.findAllSaveByPlayerByType(playerId, eventTypes[i]);
            goal = playerServices.findAllGoalByPlayerByType(playerId, eventTypes[i]);
            efficiency = (double) save / (goal + save) * 100;
            rv[i + 1][1] = String.valueOf(save);
            rv[i + 1][2] = String.valueOf(goal);
            rv[i + 1][3] = formatter.format(efficiency) + "%";
        }
        save = playerServices.findAllSaveByPlayer(playerId);
        goal = playerServices.findAllGoalByPlayer(playerId);
        efficiency = (double) save / (goal + save) * 100;
        rv[10][1] = String.valueOf(save);
        rv[10][2] = String.valueOf(goal);
        rv[10][3] = formatter.format(efficiency) + "%";
        rv[12][0] = playerServices.findAllYellowCardByPlayer(playerId) + " db";
        rv[12][1] = playerServices.findAllTwoMinutesByPlayer(playerId) + " db";
        rv[12][2] = playerServices.findAllRedCardByPlayer(playerId) + " db";
        rv[12][3] = playerServices.findAllBlueCardByPlayer(playerId) + " db";
        return rv;
    }

    private String[][] setDataMatch(long matchId) {
        NumberFormat formatter = new DecimalFormat("#0.0");
        long save, goal;
        double efficiency;
        String[][] rv = new String[][]{
                {getString(R.string.position), getString(R.string.save), getString(R.string.goal), getString(R.string.summary)},
                {getString(R.string.leftWing), "", "", ""},
                {getString(R.string.leftBack), "", "", ""},
                {getString(R.string.centralBack), "", "", ""},
                {getString(R.string.rightBack), "", "", ""},
                {getString(R.string.rightWing), "", "", ""},
                {getString(R.string.pivot), "", "", ""},
                {getString(R.string.breakIn), "", "", ""},
                {getString(R.string.fastBreak), "", "", ""},
                {getString(R.string.sevenMeters), "", "", ""},
                {getString(R.string.summary), "", "", ""},
                {getString(R.string.yellowCard), getString(R.string.twoMinutes), getString(R.string.redCard), getString(R.string.blueCard),},
                {"", "", "", ""}
        };
        for (int i = 0; i < eventTypes.length; i++) {
            save = matchServices.findAllSaveByMatchByType(matchId, eventTypes[i]);
            goal = matchServices.findAllGoalByMatchByType(matchId, eventTypes[i]);
            efficiency = (double) save / (goal + save) * 100;
            rv[i + 1][1] = String.valueOf(save);
            rv[i + 1][2] = String.valueOf(goal);
            rv[i + 1][3] = formatter.format(efficiency) + "%";
        }
        save = matchServices.findAllSaveByMatch(matchId);
        goal = matchServices.findAllGoalByMatch(matchId);
        efficiency = (double) save / (goal + save) * 100;
        rv[10][1] = String.valueOf(save);
        rv[10][2] = String.valueOf(goal);
        rv[10][3] = formatter.format(efficiency) + "%";
        rv[12][0] = matchServices.findAllYellowCardByMatch(matchId) + " db";
        rv[12][1] = matchServices.findAllTwoMinutesByMatch(matchId) + " db";
        rv[12][2] = matchServices.findAllRedCardByMatch(matchId) + " db";
        rv[12][3] = matchServices.findAllBlueCardByMatch(matchId) + " db";
        return rv;
    }
}