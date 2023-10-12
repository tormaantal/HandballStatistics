package hu.szakdolgozat.handballstatistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.pojo.Match;

public class MatchesActivity extends AppCompatActivity {

//    Szükséges változók deklarálása
    TextView toolbarTV;
    DrawerLayout matchesDrawerLayout;
    ImageView menuImageView;
    LinearLayout newMatchLinearLayout, playersLinearLayout, matchesLinearLayout, contactLinearLayout;
    RecyclerView matchRecyclerView;

    ArrayList<Match> matchArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

//        Változók inicializálása
        initMatchesActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menuImageView.setOnClickListener(view -> {
            openDrawer(matchesDrawerLayout);
        });
        newMatchLinearLayout.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
        });
        playersLinearLayout.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });

    }

//    Változók azonosítása, toolbar cím változtatása,
//    Mérkőzések feltöltése az adapterbe.
    private void initMatchesActivity() {
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.matches);
        matchesDrawerLayout = findViewById(R.id.matchesDrawerLayout);
        menuImageView = findViewById(R.id.menu);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        matchRecyclerView = findViewById(R.id.matchesRecyclerView);

        RecyclerViewMatchesAdapter adapter = new RecyclerViewMatchesAdapter(this, matchArrayList);
        matchRecyclerView.setAdapter(adapter);
        matchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        closeDrawer(matchesDrawerLayout);
    }
}