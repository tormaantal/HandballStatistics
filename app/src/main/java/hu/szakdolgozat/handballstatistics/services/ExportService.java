package hu.szakdolgozat.handballstatistics.services;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

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

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.models.Player;

public class ExportService {
    private static final EventType[] eventTypes = {
            EventType.LEFTWING, EventType.LEFTBACK, EventType.CENTERBACK,
            EventType.RIGHTBACK, EventType.RIGHTWING, EventType.PIVOT,
            EventType.FASTBREAK, EventType.BREAKIN, EventType.SEVENMETERS
    };
    private final Context context;
    private final MatchServices matchServices;
    private final PlayerServices playerServices;
    private final EventServices eventServices;
    private final long playerId;
    private final long matchId;

    public ExportService(Context context, long playerId, long matchId) {
        this.context = context;
        this.playerId = playerId;
        this.matchId = matchId;
        matchServices = new MatchServices(context);
        playerServices = new PlayerServices(context);
        eventServices = new EventServices(context);
    }

    public void generatePlayerJson() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(context, "Nincs adat!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getString(R.string.done) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("generatePlayerJson", "exception catch: " + e);
            }
        }
    }

    public void generateMatchJson() {
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
            Toast.makeText(context, context.getString(R.string.done) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("generateMatchJson", "exception catch" + e);
        }
    }

    public void generatePlayerPdf() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(context, "Nincs adat! Előbb rögzítsen mérkőzést az adott játékoshoz!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, context.getString(R.string.done) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("generatePlayerPdf", "exception catch: " + e);
            }
        }
    }

    public void generateMatchPdf() {
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
            Toast.makeText(context, context.getString(R.string.done) + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("generateMatchPdf", "exp catch: " + e);
        }
        pdfDocument.close();
    }

    public File generatePlayerPdfToEmail() {
        ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
        if (matches.isEmpty()) {
            Toast.makeText(context, "Nincs adat! Előbb rögzítsen mérkőzést az adott játékoshoz!", Toast.LENGTH_SHORT).show();
        } else {
            Player player = playerServices.findPlayerById(playerId);
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            String fileName = player.getFileName() + "_ALL_" + System.currentTimeMillis() + ".pdf";
            File externalDir = new File(context.getFilesDir().getAbsolutePath());
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
                        Log.e("generatePlayerPdfToEmail ", "inner exp catch: " + e);
                    }
                });
                pdfDocument.close();
                return file;
            } catch (IOException e) {
                Log.e("generatePlayerPdfToEmail ", "exp catch: " + e);
            }
        }
        return null;
    }

    public File generateMatchPdfToEmail() {
        Player player = playerServices.findPlayerById(playerId);
        Match match = matchServices.findMatchById(matchId);
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        String fileName = player.getFileName() + "_vs_" +
                match.getOpponent().trim() + "_" +
                System.currentTimeMillis() + ".pdf";
        File externalDir = new File(context.getFilesDir().getAbsolutePath());
        File file = new File(externalDir, fileName);
        try {
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            drawTitle(page, player + " vs " + match.getOpponent() + "(" + match.getDate() + ")");
            drawTable(page, setDataMatch(match.getMatchId()));
            pdfDocument.finishPage(page);
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            Log.e("generateMatchPdfToEmail", "exp catch: " + e);
        }
        pdfDocument.close();
        return file;
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
                {context.getString(R.string.position), context.getString(R.string.save), context.getString(R.string.goal), context.getString(R.string.summary)},
                {context.getString(R.string.leftWing), "", "", ""},
                {context.getString(R.string.leftBack), "", "", ""},
                {context.getString(R.string.centralBack), "", "", ""},
                {context.getString(R.string.rightBack), "", "", ""},
                {context.getString(R.string.rightWing), "", "", ""},
                {context.getString(R.string.pivot), "", "", ""},
                {context.getString(R.string.breakIn), "", "", ""},
                {context.getString(R.string.fastBreak), "", "", ""},
                {context.getString(R.string.sevenMeters), "", "", ""},
                {context.getString(R.string.summary), "", "", ""},
                {context.getString(R.string.yellowCard), context.getString(R.string.twoMinutes), context.getString(R.string.redCard), context.getString(R.string.blueCard),},
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
                {context.getString(R.string.position), context.getString(R.string.save), context.getString(R.string.goal), context.getString(R.string.summary)},
                {context.getString(R.string.leftWing), "", "", ""},
                {context.getString(R.string.leftBack), "", "", ""},
                {context.getString(R.string.centralBack), "", "", ""},
                {context.getString(R.string.rightBack), "", "", ""},
                {context.getString(R.string.rightWing), "", "", ""},
                {context.getString(R.string.pivot), "", "", ""},
                {context.getString(R.string.breakIn), "", "", ""},
                {context.getString(R.string.fastBreak), "", "", ""},
                {context.getString(R.string.sevenMeters), "", "", ""},
                {context.getString(R.string.summary), "", "", ""},
                {context.getString(R.string.yellowCard), context.getString(R.string.twoMinutes), context.getString(R.string.redCard), context.getString(R.string.blueCard),},
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
