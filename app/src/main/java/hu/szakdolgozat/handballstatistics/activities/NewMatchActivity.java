package hu.szakdolgozat.handballstatistics.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class NewMatchActivity extends AppCompatActivity {

    MatchServices matchServices;
    PlayerServices playerServices;
    TextView toolbarTV, tvPlayers, tvMatches, tvContact;
    Button startButton, dateButton;
    DrawerLayout newMatchDrawerLayout;
    ImageView menuImageView;
    Spinner playerSpinner;
    EditText etOpponent;
    TextInputLayout tilOpponent;
    long playerId, matchId;
    StringBuilder date;
    String opponent;
    ArrayList<String> adapterList;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_match);
        init();
        initSpinner();
        menuImageView.setOnClickListener(view ->
                newMatchDrawerLayout.openDrawer(GravityCompat.START)
        );
        tvPlayers.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
            finish();
        });
        tvMatches.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
            finish();
        });
        tvContact.setOnClickListener(view ->
                sendEmail()
        );
        dateButton.setOnClickListener(view ->
                showDatePickerDialog()
        );
        startButton.setOnClickListener(view ->
                startButtonAction()
        );
    }

    private void init() {
        matchServices = new MatchServices(this);
        playerServices = new PlayerServices(this);
        toolbarTV = findViewById(R.id.tvToolbar);
        toolbarTV.setText(R.string.new_match);
        newMatchDrawerLayout = findViewById(R.id.newMatchDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        tvPlayers = findViewById(R.id.tvPlayers);
        tvMatches = findViewById(R.id.tvMatches);
        tvContact = findViewById(R.id.tvContact);
        tilOpponent = findViewById(R.id.tilOpponent);
        etOpponent = findViewById(R.id.etOpponent);
        startButton = findViewById(R.id.startButton);
        playerSpinner = findViewById(R.id.playerSpinner);
        dateButton = findViewById(R.id.dateButton);
    }

    public void initSpinner() {
        adapterList = new ArrayList<>();
        adapterList.add("Válassz egy játékost!");
        playerServices.findAllPlayer().forEach(player -> adapterList.add(player.getName() + " (" + player.getId() + ")"));
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, adapterList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerSpinner.setAdapter(spinnerAdapter);
    }

    private void startButtonAction() {
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
        playerId = playerServices.findAllPlayer().get(playerSpinner.getSelectedItemPosition() - 1).getId();
        opponent = etOpponent.getText().toString().trim();
        matchId = matchServices.addMatch(playerId, date.toString(), opponent);
        if (matchId > 0) {
            Intent intent = new Intent(this, StartedMatchActivity.class);
            intent.putExtra("playerId", playerId);
            intent.putExtra("date", date.toString());
            intent.putExtra("opponent", opponent);
            intent.putExtra("matchId", matchId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Nem jött létre a mérkőzés!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            date = new StringBuilder();
            date.append(selectedYear);
            if (selectedMonth + 1 < 10) {
                date.append("." + 0).append(selectedMonth + 1);
            } else {
                date.append(".").append(selectedMonth + 1);
            }
            if (selectedDay < 10) {
                date.append("." + 0).append(selectedDay);
            } else {
                date.append(".").append(selectedDay);
            }
            dateButton.setHint(date);
        }, year, month, dayOfMonth).show();
    }

    private void sendEmail() {
        String[] to = {"t.anti94@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        startActivity(intent);
    }

    public void openActivity(Class<?> secondActivity) {
        Intent intent = new Intent(this, secondActivity);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (newMatchDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            newMatchDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }
}