package hu.szakdolgozat.handballstatistics.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewMatchesAdapter;
import hu.szakdolgozat.handballstatistics.services.MatchServices;

public class MatchesActivity extends AppCompatActivity implements RecyclerViewInterface {

    MatchServices matchServices;
    TextView tvToolbar, tvNewMatch, tvPlayers, tvContact;
    DrawerLayout matchesDrawerLayout;
    LinearLayout navigationDrawer;
    ImageView menuImageView, addMatchImageView;
    RecyclerView matchRecyclerView;
    RecyclerViewMatchesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        init();
        navigationDrawer.setOnClickListener(view -> {
        });
        menuImageView.setOnClickListener(view -> {
            matchesDrawerLayout.openDrawer(GravityCompat.START);
        });
        tvNewMatch.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
            finish();
        });
        tvPlayers.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
            finish();
        });
        tvContact.setOnClickListener(view -> {
            sendEmail();
        });
        addMatchImageView.setOnClickListener(view -> {

        });

    }

    private void init() {
        matchServices = new MatchServices(this);
        tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText(R.string.matches);
        navigationDrawer = findViewById(R.id.navigationDrawer);
        matchesDrawerLayout = findViewById(R.id.matchesDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        addMatchImageView = findViewById(R.id.create);
        tvPlayers = findViewById(R.id.tvPlayers);
        tvNewMatch = findViewById(R.id.tvNewMatch);
        tvContact = findViewById(R.id.tvContact);
        matchRecyclerView = findViewById(R.id.matchesRecyclerView);
        addMatchImageView.setVisibility(View.VISIBLE);
        adapter = new RecyclerViewMatchesAdapter(this, matchServices.findAllMatch(), this);
        matchRecyclerView.setAdapter(adapter);
        matchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        if (matchesDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            matchesDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("type", "match");
        intent.putExtra("matchId", matchServices.findAllMatch().get(position).getMatchId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.removeTitle)
                .setMessage(R.string.removeMatch)
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {

//                    Log.e("TAG", matchServices.findAllMatch().get(position).getMatchId() +"");
                    if (matchServices.deleteMatch(matchServices.findAllMatch().get(position).getMatchId()) == -1) {
                        Toast.makeText(this, "Hiba a törlés során!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Mérkőzés törölve!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyItemRemoved(position);
                    recreate();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {

                })
                .show();
    }
}