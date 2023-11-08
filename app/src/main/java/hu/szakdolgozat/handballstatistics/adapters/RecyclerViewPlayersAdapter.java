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
import hu.szakdolgozat.handballstatistics.models.Player;

public class RecyclerViewPlayersAdapter extends RecyclerView.Adapter<RecyclerViewPlayersAdapter.playersViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Player> playersList;


    public RecyclerViewPlayersAdapter(Context context, ArrayList<Player> playersList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.playersList = playersList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public playersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row_players, parent, false);
        return new playersViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull playersViewHolder holder, int position) {
        Player p = playersList.get(position);
        holder.rvrPlayerName.setText(p.getName());
        holder.rvrPlayerTeam.setText(p.getTeam());
        holder.rvrPlayerId.setText(String.valueOf(p.getId()));
    }

    @Override
    public int getItemCount() {
        return playersList.size();
    }

    public static class playersViewHolder extends RecyclerView.ViewHolder {
        TextView rvrPlayerId, rvrPlayerName, rvrPlayerTeam;
        public playersViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            rvrPlayerId = itemView.findViewById(R.id.rvrPlayerId);
            rvrPlayerName = itemView.findViewById(R.id.rvrPlayerName);
            rvrPlayerTeam = itemView.findViewById(R.id.rvrPlayerTeam);
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
