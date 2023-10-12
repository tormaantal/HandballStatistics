package hu.szakdolgozat.handballstatistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.pojo.Match;

public class RecyclerViewMatchesAdapter extends RecyclerView.Adapter<RecyclerViewMatchesAdapter.matchesViewHolder> {
    Context context;
    ArrayList<Match> matches;

    public RecyclerViewMatchesAdapter(Context context, ArrayList<Match> matches) {
        this.context = context;
        this.matches = matches;
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
        holder.rvrMatchDate.setText(matches.get(position).getDate().toString());
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
            rvrMatchDate = itemView.findViewById(R.id.rvrMatchDate);
            rvrMatchPlayer = itemView.findViewById(R.id.rvrMatchPlayer);
            rvrMatchOpponent = itemView.findViewById(R.id.rvrMatchOpponent);

        }
    }
}
