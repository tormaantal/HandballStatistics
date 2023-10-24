package hu.szakdolgozat.handballstatistics.activities;

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

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.database.DatabaseHelper;
import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewPlayersAdapter;
import hu.szakdolgozat.handballstatistics.models.Player;

public class PlayersActivity extends AppCompatActivity implements RecyclerViewInterface {

    //    Szükséges változók deklarálása
    TextView toolbarTV;
    DrawerLayout playersDrawerLayout;
    ImageView menuImageView, addPlayerImageView;
    LinearLayout newMatchLinearLayout, playersLinearLayout, matchesLinearLayout, contactLinearLayout;
    RecyclerView playersRecyclerView;
    TextInputLayout tilPlayerId, tilPlayerName, tilPlayerTeam;
    EditText etPlayerId, etPlayerName, etPlayerTeam;
    AlertDialog dialog;
    Button addPlayerButton;
    ArrayList<Player> playersList;
    RecyclerViewPlayersAdapter adapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

//        Változók inicializálása
        initPlayersActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menuImageView.setOnClickListener(view -> {
            playersDrawerLayout.openDrawer(GravityCompat.START);
        });
        newMatchLinearLayout.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
            finish();
        });
        matchesLinearLayout.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
            finish();
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });
//        A hozzadás gomb figyelése, dialógus kinézetének beállítása
//        és megjelenítése, a hozzáadást lekezelő függvény meghívása.
        addPlayerImageView.setOnClickListener(view -> {
            addPlayer();
        });

    }

    //    Változók azonosítása, toolbar cím változtatása,
//    hozzáadás gomb megjelenítése, Játékosok feltöltése az adapterbe.

    private void initPlayersActivity() {
        db = new DatabaseHelper(this);
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.players);
        playersDrawerLayout = findViewById(R.id.playersDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        addPlayerImageView = findViewById(R.id.addImageView);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        addPlayerImageView.setVisibility(View.VISIBLE);
        playersList = new ArrayList<>();
        readAllPlayers();
        adapter = new RecyclerViewPlayersAdapter(this, playersList, this);
        playersRecyclerView.setAdapter(adapter);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void readAllPlayers() {
        playersList.addAll(db.selectAllPlayer());
        if (playersList.isEmpty()) {
            Toast.makeText(this, R.string.emptyDB, Toast.LENGTH_SHORT).show();
        }
    }


    //    Beviteli mezők értékeivel létrehoz egy játékost és a
//    hozzáadás gomb megnyomásával az adatbázishoz adja.
    private void addPlayer() {
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
                    if (db.selectPlayerById(id) == null) {
                        db.addPlayer(new Player(id, name, team));
                        dialog.dismiss();
                        recreate();
                    } else {
                        Toast.makeText(this, "Már létezik a(z) " + id + "  engedélyszám!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

    //    Ha onPause meghívódik és
//    nyitva van a menü akkor zárja be
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
        intent.putExtra("type", "player");
        intent.putExtra("id", playersList.get(position).getId());
        startActivity(intent);
    }

    //    Ha hosszan nyomunk rá az egyik játékosra
//    akkor törli egy újboli megerősítés után.
    @Override
    public void onItemLongClick(int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.removeTitle)
                .setMessage(R.string.removePlayer)
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    db.deletePlayer(playersList.get(position).getId());
                    adapter.notifyItemRemoved(position);
                    recreate();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                })
                .show();
    }
}