package hu.szakdolgozat.handballstatistics;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewMatchActivity extends AppCompatActivity {

    //    Szükséges változók deklarálása
    TextView toolbarTV;
    DrawerLayout newMatchDrawerLayout;
    ImageView menuImageView;
    LinearLayout newMatchLinearLayout, playersLinearLayout, matchesLinearLayout, contactLinearLayout;
    Button newMatchButton, datePickerButton;
    Spinner selectPlayerSpinner;
    EditText newMatchOpponentEditText;
    int selectedPlayerId;
    String selectedDate, opponent;

    ArrayList<String> optionalPlayersArrayList;

    //    Menü megjelenítése
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //    Menü bezárása
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match);

//        Változók inicializálása
        initNewMatchActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menuImageView.setOnClickListener(view -> {
            openDrawer(newMatchDrawerLayout);
        });
        playersLinearLayout.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
        });
        matchesLinearLayout.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });

//      Játékos kiválasztásához használt
//      legördülő menü kezelése.
        selectPlayerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String text = adapterView.getItemAtPosition(position).toString();
                //Todo kiválasztott játékos ID eltárolása.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        Dátum kiváláasztásához dialógus megjelenítése.
        datePickerButton.setOnClickListener(view -> {
            try {
                showDatePickerDialog();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

//        Mérkőzés elindítása
        newMatchButton.setOnClickListener(view -> {
            openActivity(StartedMatchActivity.class);
        });
    }

    //    Változók azonosítása, toolbar cím változtatása,
//    játékos spinner feltöltése és adapter beállítása
    private void initNewMatchActivity() {
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.new_match);
        newMatchDrawerLayout = findViewById(R.id.newMatchDrawerLayout);
        menuImageView = findViewById(R.id.menu);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        newMatchOpponentEditText = findViewById(R.id.newMatchOpponentEditText);
        newMatchButton = findViewById(R.id.newMatchButton);
        selectPlayerSpinner = findViewById(R.id.selectPlayerSpinner);
        datePickerButton = findViewById(R.id.datePickerButton);
        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionalPlayersArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPlayerSpinner.setAdapter(arrayAdapter);*/
    }

    //    Dátum kiválasztása, formázása és eltárolása
    private void showDatePickerDialog() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, newYear, newMonth, newDay) ->
        {
            String dateInString=year+"-"+month+"-"+ dayOfMonth;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                formatter.parse(dateInString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            datePickerButton.setHint(dateInString);
            //Todo kiválasztott dátum eltárolása

        }, year, month, dayOfMonth);
        datePickerDialog.show();
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

    //    Egy másik Activity megnyitása.
    public void openActivity(Class secondActivity) {
        Intent intent = new Intent(this, secondActivity);
        startActivity(intent);
        finish();
    }

    //    Ha onPause meghívódik, akkor zárja be a menüt.
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(newMatchDrawerLayout);
    }
}