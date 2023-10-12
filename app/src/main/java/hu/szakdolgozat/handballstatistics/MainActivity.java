package hu.szakdolgozat.handballstatistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

//    Szükséges változók deklarálása
    DrawerLayout mainDrawerLayout;
    ImageView menu;
    LinearLayout newMatch, players, matches, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Változók inicializálása
        initMainActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menu.setOnClickListener(view -> {
            openDrawer(mainDrawerLayout);
        });
        newMatch.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
        });
        players.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
        });
        matches.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
        });
        contact.setOnClickListener(view -> {
            sendEmail();
        });
    }

//    Változók azonosítása
    private void initMainActivity() {
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout);
        menu = findViewById(R.id.menu);
        newMatch = findViewById(R.id.newMatch);
        players = findViewById(R.id.players);
        matches = findViewById(R.id.matches);
        contact = findViewById(R.id.contact);
    }

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
        closeDrawer(mainDrawerLayout);
    }
}