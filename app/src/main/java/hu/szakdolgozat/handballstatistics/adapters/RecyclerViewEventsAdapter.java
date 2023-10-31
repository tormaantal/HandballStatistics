package hu.szakdolgozat.handballstatistics.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
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
        holder.rvrEventsName.setText(String.valueOf(e.getResult()));
        holder.rvrEventsType.setText(e.getType().toString());
        holder.rvrEventsTime.setText(e.getTime());

        /*   switch (eventList.get(position).getResult()) {
            case 0:
                holder.rvrStatisticsName.setText("Védés");
                holder.rvrStatisticsTime.setText(eventList.get(position).getTime());
                holder.rvrStatisticsType.setText(typeConvert(eventList.get(position).getType()));
                break;
            case 1:

        }

*/
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String typeConvert(EventType type) {
        Resources resources = null;
        switch (type) {
            case PIVOT:
                return String.format(resources.getString(R.string.pivot));
            case BREAKIN:
                return String.format(resources.getString(R.string.breakIn));
            case CENTERBACK:
                return String.format(resources.getString(R.string.centralBack));
            case FASTBREAK:
                return String.format(resources.getString(R.string.fastBreak));
            case LEFTBACK:
                return String.format(resources.getString(R.string.leftBack));
            case LEFTWING:
                return String.format(resources.getString(R.string.leftWing));
            case RIGHTBACK:
                return String.format(resources.getString(R.string.rightBack));
            case RIGHTWING:
                return String.format(resources.getString(R.string.rightWing));
            case SEVENMETERS:
                return String.format(resources.getString(R.string.sevenMeters));
            case TWOMINUTES:
                return String.format(resources.getString(R.string.twoMinutes));
            case YELLOWCARD:
                return String.format(resources.getString(R.string.yellowCard));
            case REDCARD:
                return String.format(resources.getString(R.string.redCard));
            case BLUECARD:
                return String.format(resources.getString(R.string.blueCard));
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
