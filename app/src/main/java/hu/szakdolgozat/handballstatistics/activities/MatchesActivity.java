package hu.szakdolgozat.handballstatistics.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonSyntaxException;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewMatchesAdapter;
import hu.szakdolgozat.handballstatistics.services.ImpExpService;
import hu.szakdolgozat.handballstatistics.services.MatchServices;

public class MatchesActivity extends AppCompatActivity implements RecyclerViewInterface {
    private MatchServices matchServices;
    private ImpExpService impExpService;
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
                            try {
                                impExpService.readJsonFile(fileName);
                            } catch (JsonSyntaxException e) {
                                Toast.makeText(this, "Nem megfelelő a JSON fájl!", Toast.LENGTH_SHORT).show();
                            }
                            recreate();
                        }
                    }
                } else {
                    Toast.makeText(this, "Nincs kiválasztva fájl", Toast.LENGTH_SHORT).show();
                }
            }
    );
    private final ActivityResultLauncher<Intent> storageActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (checkPermission()) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("application/json");
                            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startPickJson.launch(intent);
                        } else {
                            Toast.makeText(MatchesActivity.this, "Hozzáférés megtagadva!", Toast.LENGTH_SHORT).show();
                        }
                    });
    private TextView tvNewMatch, tvPlayers, tvContact;
    private DrawerLayout matchesDrawerLayout;
    private ImageView menuImageView, addMatchImageView;
    private RecyclerViewMatchesAdapter adapter;

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
            if (!checkPermission()) {
                requestPermission();
            } else {
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
        matchServices = new MatchServices(this);
        impExpService = new ImpExpService(this);
        TextView tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText(R.string.matches);
        matchesDrawerLayout = findViewById(R.id.matchesDrawerLayout);
        menuImageView = findViewById(R.id.menuImageView);
        addMatchImageView = findViewById(R.id.create);
        tvPlayers = findViewById(R.id.tvPlayers);
        tvNewMatch = findViewById(R.id.tvNewMatch);
        tvContact = findViewById(R.id.tvContact);
        RecyclerView matchRecyclerView = findViewById(R.id.matchesRecyclerView);
        addMatchImageView.setVisibility(View.VISIBLE);
        adapter = new RecyclerViewMatchesAdapter(this, matchServices.findAllMatch(), this);
        matchRecyclerView.setAdapter(adapter);
        matchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void requestPermission() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package",this.getPackageName(), null);
        intent.setData(uri);
        storageActivityResultLauncher.launch(intent);
    }

    private boolean checkPermission() {
        return Environment.isExternalStorageManager();
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



