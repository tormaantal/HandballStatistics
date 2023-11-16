package hu.szakdolgozat.handballstatistics.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import hu.szakdolgozat.handballstatistics.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mainDrawerLayout;
    private ImageView menu;
    private TextView tvNewMatch, tvPlayers, tvMatches, tvContact;

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
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.exit)
                        .setMessage(R.string.exitMsg)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> finish())
                        .setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            }
        });
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
        Intent intent = new ShareCompat.IntentBuilder(this)
                .getIntent()
                .setAction(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, to);
        startActivity(intent);
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
}