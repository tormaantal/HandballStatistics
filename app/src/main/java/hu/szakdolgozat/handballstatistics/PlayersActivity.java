package hu.szakdolgozat.handballstatistics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.pojo.Player;

public class PlayersActivity extends AppCompatActivity {

    //    Szükséges változók deklarálása
    TextView toolbarTV;
    DrawerLayout playersDrawerLayout;
    ImageView menuImageView, addPlayerImageView;
    LinearLayout newMatchLinearLayout, playersLinearLayout, matchesLinearLayout, contactLinearLayout;
    RecyclerView playersRecyclerView;
    EditText addPlayerIdEditText, addPlayerNameEditText, addPlayerTeamEditText;
    AlertDialog dialog;
    Button addPlayerButton;
    ArrayList<Player> playersArrayList = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

//        Változók inicializálása
        initPlayersActivity();

//        A toolbaron-n lévő ikon és a menü opciók
//        megnyomásának figyelése, kezelése.
        menuImageView.setOnClickListener(view -> {
            openDrawer(playersDrawerLayout);
        });
        newMatchLinearLayout.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
        });
        matchesLinearLayout.setOnClickListener(view -> {
            openActivity(MatchesActivity.class);
        });
        contactLinearLayout.setOnClickListener(view -> {
            sendEmail();
        });

//        A hozzadás gomb figyelése, dialógus kinézetének beállítása
//        és megjelenítése, a hozzáadást lekezelő függvény meghívása.
        addPlayerImageView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.addPlayerTitle);
            View view1 = getLayoutInflater().inflate(R.layout.add_player_dialog, null);
            builder.setView(view1);
            dialog = builder.create();
            dialog.show();
            addPlayer();
        });
    }

    //    Beviteli mezők értékeivel létrehoz egy játékost és a
//    Hozzáadás gomb megnyomásával az adatbázishoz adja.
    private void addPlayer() {
        addPlayerButton = dialog.findViewById(R.id.addPlayerButton);
        addPlayerIdEditText = dialog.findViewById(R.id.addPlayerIdEditText);
        addPlayerNameEditText = dialog.findViewById(R.id.addPlayerNameEditText);
        addPlayerTeamEditText = dialog.findViewById(R.id.addPlayerTeamEditText);
        try {
            addPlayerButton.setOnClickListener(view -> {
                int id = Integer.parseInt(addPlayerIdEditText.getText().toString());
                String name = String.valueOf(addPlayerNameEditText.getText());
                String team = String.valueOf((addPlayerTeamEditText.getText()));
                playersArrayList.add(new Player(id, name, team));
                dialog.dismiss();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //    Változók azonosítása, toolbar cím változtatása,
//    hozzáadás gomb megjelenítése, Játékosok feltöltése az adapterbe.
    private void initPlayersActivity() {
        toolbarTV = findViewById(R.id.toolbarTV);
        toolbarTV.setText(R.string.players);
        playersDrawerLayout = findViewById(R.id.playersDrawerLayout);
        menuImageView = findViewById(R.id.menu);
        addPlayerImageView = findViewById(R.id.addPlayer);
        newMatchLinearLayout = findViewById(R.id.newMatch);
        playersLinearLayout = findViewById(R.id.players);
        matchesLinearLayout = findViewById(R.id.matches);
        contactLinearLayout = findViewById(R.id.contact);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        addPlayerImageView.setVisibility(View.VISIBLE);
        RecyclerViewPlayersAdapter adapter = new RecyclerViewPlayersAdapter(this, playersArrayList);
        playersRecyclerView.setAdapter(adapter);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        closeDrawer(playersDrawerLayout);
    }
}