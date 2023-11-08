package hu.szakdolgozat.handballstatistics.services;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.models.Player;

public class PdfServices {
    private static final EventType[] eventTypes = {
            EventType.LEFTWING, EventType.LEFTBACK, EventType.CENTERBACK,
            EventType.RIGHTBACK, EventType.RIGHTWING, EventType.PIVOT,
            EventType.FASTBREAK, EventType.BREAKIN, EventType.SEVENMETERS
    };
    PlayerServices playerServices;
    MatchServices matchServices;
    Context context;

    public PdfServices(Context context) {
        this.context = context;
        playerServices = new PlayerServices(context);
        matchServices = new MatchServices(context);
    }

    public void generatePlayerPdf(long playerId) {
        Player player = playerServices.findPlayerById(playerId);
        String title = player + " összesített adatok";
        PdfDocument pdfDocument = new PdfDocument();
        PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
        Page page = pdfDocument.startPage(pageInfo);
        String fileName = player.getFileName() + System.currentTimeMillis() + ".pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + fileName;
        File file = new File(path);
        drawTitle(page, title);
        drawTable(page, setDataPlayer(playerId));
        pdfDocument.finishPage(page);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            pdfDocument.writeTo(stream);
            ArrayList<Match> matches = matchServices.findAllMatchByPlayerId(playerId);
            matches.forEach(match -> {
                String t = player + " vs " + match.getOpponent() + " (" + match.getDate() + ")";
                Page p = pdfDocument.startPage(pageInfo);
                drawTitle(p, t);
                drawTable(p, setDataMatch(match.getMatchId()));
                pdfDocument.finishPage(p);
                try {
                    pdfDocument.writeTo(stream);
                } catch (IOException e) {
                    Log.e("TAG", e.toString());
                }
            });
            pdfDocument.close();
            stream.flush();
            stream.close();
            Toast.makeText(context, context.getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("TAG: exception catch: ", e.toString());
        }
    }

    public void generateMatchPdf(long playerId, long matchId) {
        Player player = playerServices.findPlayerById(playerId);
        Match match = matchServices.findMatchById(matchId);
        String title = player + " vs " + match.getOpponent() + "(" + match.getDate() + ")";
        PdfDocument pdfDocument = new PdfDocument();
        PageInfo pageInfo = new PageInfo.Builder(595, 842, 1).create();
        String fileName = player.getFileName() +
                match.getOpponent().trim().replaceAll(" ", "_") + "_" +
                System.currentTimeMillis() + ".pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                "/" + fileName;
        File file = new File(path);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            Page page = pdfDocument.startPage(pageInfo);
            drawTitle(page, title);
            drawTable(page, setDataMatch(match.getMatchId()));
            pdfDocument.finishPage(page);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();
            Toast.makeText(context, context.getString(R.string.pdfCreated), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawTitle(Page page, String title) {
        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(16);
        titlePaint.setFakeBoldText(true);
        float pageWidth = page.getInfo().getPageWidth();
        float x = pageWidth / 2 - titlePaint.measureText(title) / 2;
        float y = 30;
        page.getCanvas().drawText(title, x, y, titlePaint);
    }

    private void drawTable(Page page, String[][] tableData) {
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

