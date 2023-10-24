package hu.szakdolgozat.handballstatistics.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.models.Player;

public class NewMatchActivity extends AppCompatActivity {

    //    Szükséges változók deklarálása
    DatabaseHelper db;
    TextView toolbarTV;
    DrawerLayout newMatchDrawerLayout;
    ImageView menuImageView;
    LinearLayout newMatchLinearLayout, playersLinearLayout, matchesLinearLayout, contactLinearLayout;
    Button startButton, datePicker;
    Spinner playerSpinner;
    EditText etOpponent;
    TextInputLayout tilOpponent;
    int playerId;
    String date, opponent;

    ArrayList<Player> playersList;
    ArrayList<String> adapterList;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match);

//        Változók inicializálása
        initNewMatchActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menuImageView.setOnClickListener(view -> {
            newMatchDrawerLayout.openDrawer(GravityCompat.START);
        });
        playersLinearLayout.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
            finish();
        });
        matchesLinearLayout.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
            finish();
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });

//      Játékos kiválasztásához használt
//      legördülő menü kezelése.
/*        playerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String text = adapterView.getItemAtPosition(position).toString();
                //Todo kiválasztott játékos ID eltárolása.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

//        Dátum kiváláasztásához dialógus megjelenítése.
        datePicker.setOnClickListener(view -> {
            try {
                showDatePickerDialog();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

//        Mérkőzés elindítása
        startButton.setOnClickListener(view -> {
            if (playerSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Válassz ki egy játékost!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (date == null) {
                Toast.makeText(this, "Válassz ki időpontot!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etOpponent.getText().toString().isEmpty()) {
                tilOpponent.setError("Nem lehet üres!");
                return;
            }
            playerId = playersList.get(playerSpinner.getSelectedItemPosition() - 1).getId();
            opponent = etOpponent.getText().toString().trim();
            Intent intent = new Intent(this, StartedMatchActivity.class);
            intent.putExtra("id", playerId);
            intent.putExtra("date", date);
            intent.putExtra("opponent", opponent);
            startActivity(intent);
        });
    }

    //    Változók azonosítása, toolbar cím változtatása,
//    játékos spinner feltöltése és adapter beállítása
    private void initNewMatchActivity() {
        db = new DatabaseHelper(this);
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.new_match);
        newMatchDrawerLayout = findViewById(R.id.newMatchDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        tilOpponent = findViewById(R.id.tilOpponent);
        etOpponent = findViewById(R.id.etOpponent);
        startButton = findViewById(R.id.startButton);
        playerSpinner = findViewById(R.id.playerSpinner);
        datePicker = findViewById(R.id.datePicker);
        playersList = new ArrayList<>();
        adapterList = new ArrayList<>();
        readAllPlayers();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, adapterList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerSpinner.setAdapter(spinnerAdapter);
    }

    public void readAllPlayers() {
        adapterList.add("Válassz egy játékost!");
        playersList.addAll(db.selectAllPlayer());
        if (!playersList.isEmpty()) {
            playersList.forEach(player -> {
                adapterList.add(player.getName() + " (" + player.getId() + ")");
            });
        }
    }

    //    Dátum kiválasztása, formázása és eltárolása
    private void showDatePickerDialog() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, newYear, newMonth, newDay) ->
        {
            String dateInString = year + "-" + month + "-" + dayOfMonth;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                formatter.parse(dateInString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            this.datePicker.setHint(dateInString);
            //Todo kiválasztott dátum eltárolása

        }, year, month, dayOfMonth);
        date = year + "-" + month + "-" + dayOfMonth;
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
    }

    //    Ha onPause meghívódik, akkor zárja be a menüt.
    @Override
    protected void onPause() {
        if (newMatchDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            newMatchDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }
}