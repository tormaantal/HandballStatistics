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
import hu.szakdolgozat.handballstatistics.models.Match;
import hu.szakdolgozat.handballstatistics.services.PlayerServices;

public class RecyclerViewMatchesAdapter extends RecyclerView.Adapter<RecyclerViewMatchesAdapter.matchesViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Match> matchesList;
    PlayerServices playerServices;

    public RecyclerViewMatchesAdapter(Context context, ArrayList<Match> matchesList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.matchesList = matchesList;
        this.recyclerViewInterface =recyclerViewInterface;
        playerServices = new PlayerServices(context);
    }

    @NonNull
    @Override
    public matchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_matches, parent, false);
        return new matchesViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull matchesViewHolder holder, int position) {
        Match m = matchesList.get(position);
        holder.rvrMatchDate.setText(m.getDate());
        holder.rvrMatchPlayer.setText(playerServices.findPlayerById(m.getPlayerId()).toString());
        holder.rvrMatchOpponent.setText(m.getOpponent());
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    public static class matchesViewHolder extends RecyclerView.ViewHolder {
        TextView rvrMatchDate,rvrMatchPlayer,rvrMatchOpponent;
        public matchesViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            rvrMatchDate = itemView.findViewById(R.id.rvrMatchDate);
            rvrMatchPlayer = itemView.findViewById(R.id.rvrMatchPlayer);
            rvrMatchOpponent = itemView.findViewById(R.id.rvrMatchOpponent);
            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(position);
                    }
                }
            });
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
