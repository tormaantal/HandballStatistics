package hu.szakdolgozat.handballstatistics.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import hu.szakdolgozat.handballstatistics.R;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mainDrawerLayout;
    ImageView menu;
    TextView tvNewMatch, tvPlayers, tvMatches, tvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMainActivity();
        menu.setOnClickListener(view ->
                mainDrawerLayout.openDrawer(GravityCompat.START)
        );
        tvNewMatch.setOnClickListener(view ->
                openActivity(NewMatchActivity.class)
        );
        tvPlayers.setOnClickListener(view ->
                openActivity(PlayersActivity.class)
        );
        tvMatches.setOnClickListener(view ->
                openActivity(MatchesActivity.class)
        );
        tvContact.setOnClickListener(view ->
                sendEmail()
        );
    }

    private void initMainActivity() {
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout);
        menu = findViewById(R.id.menuImageView);
        tvNewMatch = findViewById(R.id.tvNewMatch);
        tvPlayers = findViewById(R.id.tvPlayers);
        tvMatches = findViewById(R.id.tvMatches);
        tvContact = findViewById(R.id.tvContact);
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

    public void openActivity(Class<?> secondActivity) {
        Intent intent = new Intent(this, secondActivity);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.exitMsg)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> MainActivity.super.onBackPressed())
                .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}