package hu.szakdolgozat.handballstatistics.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewMatchesAdapter;
import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.models.Player;
import hu.szakdolgozat.handballstatistics.services.EventServices;
import hu.szakdolgozat.handballstatistics.services.MatchServices;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class MatchesActivity extends AppCompatActivity implements RecyclerViewInterface {
    private static final int PERMISSION_REQUEST_CODE = 1;
    PlayerServices playerServices;
    MatchServices matchServices;
    EventServices eventServices;
    private final ActivityResultLauncher<Intent> startPickJson = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            assert uri.getPath() != null;
                            String fileName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
                            readJsonFile(fileName);
                        }
                    }
                } else {
                    Toast.makeText(this, "Nincs kiválasztva fájl", Toast.LENGTH_SHORT).show();
                }
            }
    );
    TextView tvToolbar, tvNewMatch, tvPlayers, tvContact;
    DrawerLayout matchesDrawerLayout;
    ImageView menuImageView, addMatchImageView;
    RecyclerView matchRecyclerView;
    RecyclerViewMatchesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        init();
        menuImageView.setOnClickListener(view ->
                matchesDrawerLayout.openDrawer(GravityCompat.START)
        );
        tvNewMatch.setOnClickListener(view -> {
            openActivity(NewMatchActivity.class);
            finish();
        });
        tvPlayers.setOnClickListener(view -> {
            openActivity(PlayersActivity.class);
            finish();
        });
        tvContact.setOnClickListener(view ->
                sendEmail()
        );
        addMatchImageView.setOnClickListener(view -> {
            if (!checkPermission()){
                requestPermission();
            }else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startPickJson.launch(intent);
            }
        });
    }

    private void init() {
        playerServices = new PlayerServices(this);
        matchServices = new MatchServices(this);
        eventServices = new EventServices(this);
        tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText(R.string.matches);
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

    private void readJsonFile(String fileName) {
        JsonObject combinedJson = null;
        File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(externalDir, fileName);
        try (Reader reader = new FileReader(file)) {
            combinedJson = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            Log.e("TAG", e.toString());
        }
        if (combinedJson != null) {
            Gson gson = new Gson();
            Type listTypeMatch = new TypeToken<List<Match>>() {
            }.getType();
            Type listTypeEvent = new TypeToken<List<Event>>() {
            }.getType();
            Player player = gson.fromJson(combinedJson.get("Player"), Player.class);
            try {
                Match oldMatch = gson.fromJson(combinedJson.get("Matches"), Match.class);
                List<Event> events = combinedJson.has("Events") ? gson.fromJson(combinedJson.getAsJsonArray("Events"), listTypeEvent) : null;
                if (playerServices.findPlayerById(player.getId()) != null) {
                    long newMatchId = matchServices.addMatch(player.getId(), oldMatch.getDate(), oldMatch.getOpponent());
                    assert events != null;
                    events.forEach(event -> {
                        if (event.getMatchId() == oldMatch.getMatchId()) {
                            eventServices.addEvent(newMatchId, event.getTime(), event.getType(), event.getResult());
                        }
                    });
                } else {
                    playerServices.addPlayer(player.getId(), player.getName(), player.getTeam());
                    long newMatchId = matchServices.addMatch(player.getId(), oldMatch.getDate(), oldMatch.getOpponent());
                    assert events != null;
                    events.forEach(event -> {
                        if (event.getMatchId() == oldMatch.getMatchId()) {
                            eventServices.addEvent(newMatchId, event.getTime(), event.getType(), event.getResult());
                        }
                    });
                }
                Toast.makeText(this, "Adatok importálva!", Toast.LENGTH_SHORT).show();
                recreate();
            } catch (JsonSyntaxException e) {
                List<Match> matches = combinedJson.has("Matches") ? gson.fromJson(combinedJson.getAsJsonArray("Matches"), listTypeMatch) : null;
                List<Event> events = combinedJson.has("Events") ? gson.fromJson(combinedJson.getAsJsonArray("Events"), listTypeEvent) : null;
                if (playerServices.findPlayerById(player.getId()) != null) {
                    assert matches != null;
                    matches.forEach(oldMatch -> {
                        long newMatchId = matchServices.addMatch(player.getId(), oldMatch.getDate(), oldMatch.getOpponent());
                        assert events != null;
                        events.forEach(event -> {
                            if (event.getMatchId() == oldMatch.getMatchId()) {
                                eventServices.addEvent(newMatchId, event.getTime(), event.getType(), event.getResult());
                            }
                        });
                    });
                } else {
                    playerServices.addPlayer(player.getId(), player.getName(), player.getTeam());
                    assert matches != null;
                    matches.forEach(match -> {
                        long newMatchId = matchServices.addMatch(player.getId(), match.getDate(), match.getOpponent());
                        assert events != null;
                        events.forEach(event -> {
                            if (event.getMatchId() == match.getMatchId()) {
                                eventServices.addEvent(newMatchId, event.getTime(), event.getType(), event.getResult());
                            }
                        });
                    });
                }
                Toast.makeText(this, "Adatok importálva!", Toast.LENGTH_SHORT).show();
                recreate();
            }
        } else {
            Toast.makeText(this, "No data found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("PERMISSIONS:", Arrays.toString(permissions) + " - " + grantResults[0] + "\n" + grantResults[1]);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (writeStorage && readStorage) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startPickJson.launch(intent);
            } else {
                Log.e("TAG", "Engedély megtagadva");
                finish();
            }
        }
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
        if (matchesDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            matchesDrawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onPause();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("methodType", "match");
        intent.putExtra("playerId", matchServices.findAllMatch().get(position).getPlayerId());
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



