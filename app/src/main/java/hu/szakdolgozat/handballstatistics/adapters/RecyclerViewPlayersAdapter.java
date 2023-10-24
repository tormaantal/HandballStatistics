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
        holder.rvrPlayerName.setText(playersList.get(position).getName());
        holder.rvrPlayerTeam.setText(playersList.get(position).getTeam());
        holder.rvrPlayerId.setText(String.valueOf(playersList.get(position).getId()));

        //    Ha rányomunk az egyik játékosra
//    akkor megjeleníti az összesített statisztikáját.
    }


    @Override
    public int getItemCount() {
        return playersList.size();
    }

    public static class playersViewHolder extends RecyclerView.ViewHolder {
        TextView rvrPlayerId, rvrPlayerName, rvrPlayerTeam;

        public playersViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            rvrPlayerId = itemView.findViewById(R.id.rvrStatisticsType);
            rvrPlayerName = itemView.findViewById(R.id.rvrStatisticsName);
            rvrPlayerTeam = itemView.findViewById(R.id.rvrStatisticsTime);
            itemView.setOnClickListener(view -> {
                if (recyclerViewInterface != null){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
            itemView.setOnLongClickListener(view -> {
                if (recyclerViewInterface != null){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemLongClick(pos);
                    }
                }
                return true;
            });
        }
    }
}
