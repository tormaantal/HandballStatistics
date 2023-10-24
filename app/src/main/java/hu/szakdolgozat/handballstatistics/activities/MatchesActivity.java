package hu.szakdolgozat.handballstatistics.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewMatchesAdapter;
import hu.szakdolgozat.handballstatistics.models.Match;

public class MatchesActivity extends AppCompatActivity implements RecyclerViewInterface {

    //    Szükséges változók deklarálása
    TextView toolbarTV;
    DrawerLayout matchesDrawerLayout;
    ImageView menuImageView, addMatchImageView;
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
            matchesDrawerLayout.openDrawer(GravityCompat.START);
        });
        newMatchLinearLayout.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
            finish();
        });
        playersLinearLayout.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
            finish();
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });
        addMatchImageView.setOnClickListener(view -> {

        });

    }


    //    Változók azonosítása, toolbar cím változtatása,
//    Mérkőzések feltöltése az adapterbe.
    private void initMatchesActivity() {
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.matches);
        matchesDrawerLayout = findViewById(R.id.matchesDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        addMatchImageView = findViewById(R.id.addImageView);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        matchRecyclerView = findViewById(R.id.matchesRecyclerView);
        addMatchImageView.setVisibility(View.VISIBLE);
        RecyclerViewMatchesAdapter adapter = new RecyclerViewMatchesAdapter(this, matchArrayList,this);
        matchRecyclerView.setAdapter(adapter);
        matchRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        if (matchesDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            matchesDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("matchId", matchArrayList.get(position).getMatchId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {

    }
}