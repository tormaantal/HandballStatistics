package hu.szakdolgozat.handballstatistics.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewPlayersAdapter;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class PlayersActivity extends AppCompatActivity implements RecyclerViewInterface {
    PlayerServices playerServices;
    DrawerLayout playersDrawerLayout;
    LinearLayout navigationDrawer;
    ImageView menuImageView, addPlayerImageView;
    TextView tvToolbar,tvNewMatch, tvMatches, tvContact;
    TextInputLayout tilPlayerId, tilPlayerName, tilPlayerTeam;
    EditText etPlayerId, etPlayerName, etPlayerTeam;
    Button addPlayerButton;
    AlertDialog dialog;
    RecyclerView playersRecyclerView;
    RecyclerViewPlayersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        init();
        navigationDrawer.setOnClickListener(view -> {});
        menuImageView.setOnClickListener(view -> {
            playersDrawerLayout.openDrawer(GravityCompat.START);
        });
        tvNewMatch.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
            finish();
        });
        tvMatches.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
            finish();
        });
        tvContact.setOnClickListener(view -> {
            sendEmail();
        });
        addPlayerImageView.setOnClickListener(view -> {
            addPlayerAction();
        });

    }

    private void init() {
        playerServices = new PlayerServices(this);
        tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText(R.string.players);
        navigationDrawer = findViewById(R.id.navigationDrawer);
        playersDrawerLayout = findViewById(R.id.playersDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        addPlayerImageView = findViewById(R.id.create);
        tvNewMatch = findViewById(R.id.tvNewMatch);
        tvMatches = findViewById(R.id.tvMatches);
        tvContact = findViewById(R.id.tvContact);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        addPlayerImageView.setVisibility(View.VISIBLE);
        adapter = new RecyclerViewPlayersAdapter(this, playerServices.findAllPlayer(), this);
        playersRecyclerView.setAdapter(adapter);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addPlayerAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.addPlayerTitle);
        View view1 = getLayoutInflater().inflate(R.layout.add_player_dialog, null);
        builder.setView(view1);
        dialog = builder.create();
        dialog.show();
        addPlayerButton = dialog.findViewById(R.id.addPlayerButton);
        etPlayerId = dialog.findViewById(R.id.etPlayerId);
        etPlayerName = dialog.findViewById(R.id.etPlayerName);
        etPlayerTeam = dialog.findViewById(R.id.etPlayerTeam);
        tilPlayerId = dialog.findViewById(R.id.tilPlayerId);
        tilPlayerName = dialog.findViewById(R.id.tilPlayerName);
        tilPlayerTeam = dialog.findViewById(R.id.tilPlayerTeam);
        addPlayerButton.setOnClickListener(view -> {
            int id;
            String name = "", team = "";
            if (etPlayerName.getText().toString().trim().isEmpty()) {
                tilPlayerName.setError("Nem lehet üres!");
            } else {
                name = etPlayerName.getText().toString().trim();
                tilPlayerName.setError(null);
            }
            if (etPlayerTeam.getText().toString().trim().isEmpty()) {
                tilPlayerTeam.setError("Nem lehet üres!");
            } else {
                team = etPlayerTeam.getText().toString().trim();
                tilPlayerTeam.setError(null);
            }
            if (etPlayerId.getText().toString().trim().isEmpty()) {
                tilPlayerId.setError("Nem lehet üres!");
            } else {
                tilPlayerId.setError(null);
                id = Integer.parseInt(etPlayerId.getText().toString().trim());
                if (!name.isEmpty() && !team.isEmpty()) {
                    if (playerServices.findPlayerById(id) == null) {
                        if (playerServices.addPlayer(id, name, team) == -1) {
                            Toast.makeText(this, "Hiba történt!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Játékos létrehozva!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                        recreate();
                    } else {
                        Toast.makeText(this, "Már létezik a(z) " + id + "  engedélyszám!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
        if (playersDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            playersDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("type", "playerId");
        intent.putExtra("id", playerServices.findAllPlayer().get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.removeTitle)
                .setMessage(R.string.removePlayer)
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    if (playerServices.deletePlayer(playerServices.findAllPlayer().get(position).getId()) == -1) {
                        Toast.makeText(this, "Hiba a törlés során!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Játékos törölve!", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyItemRemoved(position);
                    recreate();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                })
                .show();
    }
}