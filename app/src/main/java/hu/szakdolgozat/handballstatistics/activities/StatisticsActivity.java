package hu.szakdolgozat.handballstatistics.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.R;

public class StatisticsActivity extends AppCompatActivity {

    DatabaseHelper db;
    int id;
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
        initStatisticsActivity();
        loadstatistics();


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

    private void initStatisticsActivity() {
        db = new DatabaseHelper(this);
        statisticsBackIV = findViewById(R.id.statisticsBackIV);
        staticsDetailsTV = findViewById(R.id.staticsDetailsTV);
        statisticsExportButton = findViewById(R.id.statisticsExportButton);
    }

    private void loadstatistics() {
        String type = getIntent().getStringExtra("type");
        if (Objects.equals(type, "player")) {
            id = getIntent().getIntExtra("id", 0);
            staticsDetailsTV.setText(db.selectPlayerById(id).toString());
            //Todo játékos összes meccsének statisztikájának lekérése
            return;
        }
        if (Objects.equals(type, "match")){
            //Todo adott mérkőzés statisztikájának lekérése
            return;
        }
    }

    //    Email küldése a saját email címemre, email küldésre
//    képes alkalmazások kiválasztási lehetőséggel.
    private void sendEmail() {
        String[] to = {"t.anti94@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}