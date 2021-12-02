package com.example.casttotv.openvpn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.casttotv.R;
import com.example.casttotv.openvpn.interfaces.NavItemClickListener;
import com.example.casttotv.openvpn.model.Server;

import java.util.ArrayList;

public class ServerListRVAdapter extends RecyclerView.Adapter<ServerListRVAdapter.MyViewHolder> {

    private ArrayList<Server> serverLists;
    private Context mContext;
    private final NavItemClickListener listener;

    public ServerListRVAdapter(ArrayList<Server> serverLists, Context context, NavItemClickListener listener) {
        this.serverLists = serverLists;
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.server_list_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.serverCountry.setText(serverLists.get(holder.getAbsoluteAdapterPosition()).getCountry());
        Glide.with(mContext)
                .load(serverLists.get(holder.getAbsoluteAdapterPosition()).getFlagUrl())
                .into(holder.serverIcon);

        holder.serverItemLayout.setOnClickListener(v -> listener.clickedItem(holder.getAbsoluteAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return serverLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout serverItemLayout;
        ImageView serverIcon;
        TextView serverCountry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serverItemLayout = itemView.findViewById(R.id.serverItemLayout);
            serverIcon = itemView.findViewById(R.id.iconImg);
            serverCountry = itemView.findViewById(R.id.countryTv);
        }
    }
}
