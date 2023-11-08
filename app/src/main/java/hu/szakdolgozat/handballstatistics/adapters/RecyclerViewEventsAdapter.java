package hu.szakdolgozat.handballstatistics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.R;
import hu.szakdolgozat.handballstatistics.RecyclerViewInterface;
import hu.szakdolgozat.handballstatistics.models.Event;
import hu.szakdolgozat.handballstatistics.models.EventType;

public class RecyclerViewEventsAdapter extends RecyclerView.Adapter<RecyclerViewEventsAdapter.eventsViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Event> eventList;

    public RecyclerViewEventsAdapter(Context context, ArrayList<Event> eventList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.eventList = eventList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public eventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_events, parent, false);
        return new eventsViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull eventsViewHolder holder, int position) {
        Event e = eventList.get(position);
        holder.rvrEventsName.setText(resultToString(e.getResult()));
        holder.rvrEventsType.setText(typeToString(e.getType()));
        holder.rvrEventsTime.setText(e.getTime());
    }

    private String resultToString(int result) {
        switch (result) {
            case 0:
                return context.getString(R.string.goal) + "!";
            case 1:
                return context.getString(R.string.save) + "!";
            case 2:
                return "Figyelmeztetés!";
            case 3:
                return "Kiállítás!";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String typeToString(EventType type) {
        switch (type) {
            case PIVOT:
                return context.getString(R.string.pivot);
            case BREAKIN:
                return context.getString(R.string.breakIn);
            case CENTERBACK:
                return context.getString(R.string.centralBack);
            case FASTBREAK:
                return context.getString(R.string.fastBreak);
            case LEFTBACK:
                return context.getString(R.string.leftBack);
            case LEFTWING:
                return context.getString(R.string.leftWing);
            case RIGHTBACK:
                return context.getString(R.string.rightBack);
            case RIGHTWING:
                return context.getString(R.string.rightWing);
            case SEVENMETERS:
                return context.getString(R.string.sevenMeters);
            case YELLOWCARD:
                return context.getString(R.string.yellowCard) + "!";
            case TWOMINUTES:
                return context.getString(R.string.twoMinutes) + "!";
            default:
                return "";
        }
    }

    public static class eventsViewHolder extends RecyclerView.ViewHolder {

        TextView rvrEventsName, rvrEventsType, rvrEventsTime;

        public eventsViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            rvrEventsName = itemView.findViewById(R.id.rvrEventsName);
            rvrEventsType = itemView.findViewById(R.id.rvrEventsType);
            rvrEventsTime = itemView.findViewById(R.id.rvrEventsTime);
            itemView.setOnLongClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemLongClick(position);
                    }
                }
                return true;
            });
        }
    }
}
