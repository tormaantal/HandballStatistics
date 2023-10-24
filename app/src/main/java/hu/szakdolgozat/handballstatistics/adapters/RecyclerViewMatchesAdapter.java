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

public class RecyclerViewMatchesAdapter extends RecyclerView.Adapter<RecyclerViewMatchesAdapter.matchesViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Match> matches;

    public RecyclerViewMatchesAdapter(Context context, ArrayList<Match> matches, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.matches = matches;
        this.recyclerViewInterface =recyclerViewInterface;
    }

    @NonNull
    @Override
    public RecyclerViewMatchesAdapter.matchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_matches, parent, false);
        return new matchesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewMatchesAdapter.matchesViewHolder holder, int position) {
        holder.rvrMatchDate.setText(matches.get(position).getDate());
        holder.rvrMatchPlayer.setText(String.valueOf(matches.get(position).getPlayerId()));
        holder.rvrMatchOpponent.setText(matches.get(position).getOpponent());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static class matchesViewHolder extends RecyclerView.ViewHolder {

        TextView rvrMatchDate,rvrMatchPlayer,rvrMatchOpponent;
        public matchesViewHolder(@NonNull View itemView) {
            super(itemView);
            rvrMatchDate = itemView.findViewById(R.id.rvrMatchPlayerName);
            rvrMatchPlayer = itemView.findViewById(R.id.rvrMatchOpponent);
            rvrMatchOpponent = itemView.findViewById(R.id.rvrStatisticsType);

        }
    }
}
