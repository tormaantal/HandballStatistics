package hu.szakdolgozat.handballstatistics.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.adapters.RecyclerViewEventsAdapter;
import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.EventType;
import hu.szakdolgozat.handballstatistics.services.EventServices;
import hu.szakdolgozat.handballstatistics.services.MatchServices;

public class StartedMatchActivity extends AppCompatActivity implements RecyclerViewInterface {

    MatchServices matchServices;
    EventServices eventServices;
    NumberPicker npMinutes;
    ImageView addSave, addGoal;
    Button endMatch, addYellowCard, addTwoMinutes, addRedCard, addBlueCard;
    RadioButton rbLeftWing, rbLeftBack, rbPivot, rbSevenMeters, rbCentralBack,
            rbRightBack, rbRightWing, rbBreakIn, rbFastBreak;
    long playerId, matchId, eventId;
    String date, opponent;
    ArrayList<Event> events;
    EventType type;
    RecyclerView recyclerView;
    RecyclerViewEventsAdapter adapter;
    private int twominutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_match);
        init();
        setOnActionListener();
    }

    private void init() {
        matchServices = new MatchServices(this);
        eventServices = new EventServices(this);
        playerId = getIntent().getLongExtra("playerId", -1);
        date = getIntent().getStringExtra("date");
        opponent = getIntent().getStringExtra("opponent");
        matchId = getIntent().getLongExtra("matchId", -1);
        events = new ArrayList<>();
        addGoal = findViewById(R.id.addGoal);
        addSave = findViewById(R.id.addSave);
        endMatch = findViewById(R.id.endMatch);
        npMinutes = findViewById(R.id.npMinutes);
        addYellowCard = findViewById(R.id.addYellowCard);
        addTwoMinutes = findViewById(R.id.addTwoMinutes);
        addRedCard = findViewById(R.id.addRedCard);
        addBlueCard = findViewById(R.id.addBlueCard);
        npMinutes.setMinValue(1);
        npMinutes.setMaxValue(59);
        rbLeftWing = findViewById(R.id.rbLeftWing);
        rbLeftBack = findViewById(R.id.rbLeftBack);
        rbPivot = findViewById(R.id.rbPivot);
        rbSevenMeters = findViewById(R.id.rbSevenMeters);
        rbCentralBack = findViewById(R.id.rbCentralBack);
        rbRightBack = findViewById(R.id.rbRightBack);
        rbRightWing = findViewById(R.id.rbRightWing);
        rbBreakIn = findViewById(R.id.rbBreakIn);
        rbFastBreak = findViewById(R.id.rbFastBreak);
        recyclerView = findViewById(R.id.statisticsRecyclerView);
        twominutes = 0;
        events = new ArrayList<>();
        adapter = new RecyclerViewEventsAdapter(this, events, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setOnActionListener() {
        addSave.setOnClickListener(view -> {
            addSave.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_image_view));
            if (isTypeSelected()) {
                setAllRadioButtonCheckedFalse();
                eventId = eventServices.addEvent(matchId, getTime(), type, 1);
                events.add(0, new Event(eventId, matchId, getTime(), type, 1));
                events.sort((e1, e2) -> e2.getTime().compareToIgnoreCase(e1.getTime()));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                type = null;
            } else {
                Toast.makeText(this, "Válassz ki egy pozíciót!", Toast.LENGTH_SHORT).show();
            }
        });
        addGoal.setOnClickListener(view -> {
            addGoal.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation_image_view));
            if (isTypeSelected()) {
                setAllRadioButtonCheckedFalse();
                eventId = eventServices.addEvent(matchId, getTime(), type, 0);
                events.add(0, new Event(eventId, matchId, getTime(), type, 0));
                events.sort((e1, e2) -> e2.getTime().compareToIgnoreCase(e1.getTime()));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                type = null;
            } else {
                Toast.makeText(this, "Válassz ki egy pozíciót!", Toast.LENGTH_SHORT).show();
            }
        });
        endMatch.setOnClickListener(view -> {
            Toast.makeText(this, "Mérkőzés eltárolva!", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(this, MatchesActivity.class);
            startActivity(intent);
        });
        rbLeftWing.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.LEFTWING;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbLeftBack.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.LEFTBACK;
                rbLeftWing.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbCentralBack.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.CENTERBACK;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbLeftWing.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbRightBack.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.RIGHTBACK;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbLeftWing.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbRightWing.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.RIGHTWING;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbLeftWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbPivot.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.PIVOT;
                rbLeftBack.setChecked(false);
                rbLeftWing.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbSevenMeters.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.SEVENMETERS;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbLeftWing.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbBreakIn.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.BREAKIN;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbLeftWing.setChecked(false);
                rbFastBreak.setChecked(false);
            }
        });
        rbFastBreak.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                type = EventType.FASTBREAK;
                rbLeftBack.setChecked(false);
                rbPivot.setChecked(false);
                rbSevenMeters.setChecked(false);
                rbCentralBack.setChecked(false);
                rbRightBack.setChecked(false);
                rbRightWing.setChecked(false);
                rbBreakIn.setChecked(false);
                rbLeftWing.setChecked(false);
            }
        });
        addYellowCard.setOnClickListener(view -> {
            eventId = eventServices.addEvent(matchId, getTime(), EventType.YELLOWCARD, 2);
            events.add(0, new Event(eventId, matchId, getTime(), EventType.YELLOWCARD, 2));
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            addYellowCard.setEnabled(false);
            addYellowCard.setAlpha(0.5F);
        });
        addTwoMinutes.setOnClickListener(view -> {
            if (++twominutes == 3) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.redCard)
                        .setMessage("A játékost véglegesen kiállították!")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            eventServices.addEvent(matchId, getTime(), EventType.REDCARD, 4);
                            finish();
                            Intent intent = new Intent(this, MatchesActivity.class);
                            startActivity(intent);
                        })
                        .setCancelable(false)
                        .show();
            } else {
                eventId = eventServices.addEvent(matchId, getTime(), EventType.TWOMINUTES, 3);
                events.add(0, new Event(eventId, matchId, getTime(), EventType.TWOMINUTES, 3));
                events.sort((e1, e2) -> e2.getTime().compareToIgnoreCase(e1.getTime()));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        });
        addRedCard.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.redCard)
                        .setMessage("A játékost véglegesen kiállították!")
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            eventServices.addEvent(matchId, getTime(), EventType.REDCARD, 4);
                            finish();
                            Intent intent = new Intent(this, MatchesActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                        })
                        .show()
        );
        addBlueCard.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.blueCard)
                        .setMessage("A játékost véglegesen kiállították!")
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            eventServices.addEvent(matchId, getTime(), EventType.REDCARD, 4);
                            eventServices.addEvent(matchId, getTime(), EventType.BLUECARD, 5);
                            finish();
                            Intent intent = new Intent(this, MatchesActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton(R.string.no, (dialogInterface, i) -> {
                        })
                        .show()
        );
    }

    private void setAllRadioButtonCheckedFalse() {
        rbLeftWing.setChecked(false);
        rbLeftBack.setChecked(false);
        rbPivot.setChecked(false);
        rbSevenMeters.setChecked(false);
        rbCentralBack.setChecked(false);
        rbRightBack.setChecked(false);
        rbRightWing.setChecked(false);
        rbBreakIn.setChecked(false);
        rbFastBreak.setChecked(false);
    }

    private boolean isTypeSelected() {
        return type != null;
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Megkaszítás!")
                .setMessage("Biztos megszakítja a mérkőzést? Minden eddigi adatot elveszít!")
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    matchServices.deleteMatch(matchId);
                    super.onBackPressed();
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {

                })
                .show();
    }

    private String getTime() {
        return npMinutes.getValue() > 9 ? npMinutes.getValue() + ". perc" : "0" + npMinutes.getValue() + ". perc";
    }

    @Override
    public void onItemClick(int position) {
    }

    @Override
    public void onItemLongClick(int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.removeTitle)
                .setMessage(R.string.removeEvent)
                .setIcon(R.drawable.baseline_delete_24)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    if (eventServices.deleteEvent(events.get(position).getEventId()) == -1) {
                        Toast.makeText(this, "Hiba a törlés során!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (events.get(position).getResult() == 2) {
                            addYellowCard.setEnabled(true);
                            addYellowCard.setAlpha(1.0F);
                        }
                        if (events.get(position).getResult() == 3) {
                            twominutes--;
                        }
                        events.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.no, (dialogInterface, i) -> {

                })
                .show();
    }
}