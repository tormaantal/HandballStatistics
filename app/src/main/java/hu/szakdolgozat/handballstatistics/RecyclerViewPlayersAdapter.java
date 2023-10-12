package hu.szakdolgozat.handballstatistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.szakdolgozat.handballstatistics.pojo.Player;

public class RecyclerViewPlayersAdapter extends RecyclerView.Adapter<RecyclerViewPlayersAdapter.playersViewHolder> {
    Context context;
    ArrayList<Player> players;


    public RecyclerViewPlayersAdapter(Context context, ArrayList<Player> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public playersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_players, parent, false);
        return new playersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull playersViewHolder holder, int position) {
        holder.rvrPlayerName.setText(players.get(position).getName());
        holder.rvrPlayerTeam.setText(players.get(position).getTeam());
        holder.rvrPlayerId.setText(String.valueOf(players.get(position).getPlayerId()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class playersViewHolder extends RecyclerView.ViewHolder {
        TextView rvrPlayerId, rvrPlayerName, rvrPlayerTeam;


        public playersViewHolder(@NonNull View itemView) {
            super(itemView);
            rvrPlayerId = itemView.findViewById(R.id.rvrMatchOpponent);
            rvrPlayerName = itemView.findViewById(R.id.rvrPlayerName);
            rvrPlayerTeam = itemView.findViewById(R.id.rvrPlayerTeam);

        }
    }
}
