package com.nyi.yuwifipassword;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IN-3442 on 12-Oct-15.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.KeyHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<Wifi> wifiArrayList;
    private static MyClickListener myClickListener;
    Context context;

    @Override
    public KeyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        KeyHolder keyHolder = new KeyHolder(view);
        return keyHolder;
    }

    @Override
    public void onBindViewHolder(KeyHolder holder, int position) {
        holder.ssid.setText(wifiArrayList.get(position).getSSID());
        holder.status.setText(wifiArrayList.get(position).getStatus());
        String status=wifiArrayList.get(position).getStatus();
        if(status.compareTo("Available")==0 || status.compareTo("Connected")==0){
            holder.ssid.setTextColor(Color.parseColor("#8bc34a"));
            holder.v.setBackgroundResource(R.drawable.icon_wifi_open);
        }else{
            holder.ssid.setTextColor(Color.parseColor("#bdbdbd"));
            holder.v.setBackgroundResource(R.drawable.icon_wifi_close);
        }


        //holder.v.setBackgroundColor(Color.parseColor("#000000"));
        //holder.v.setBackgroundResource(R.drawable.cycle);
       // holder.card.setBackground(Color.parseColor("#e0e0e0"));
        //holder.card.setCardBackgroundColor(Color.parseColor("#e0e0e0"));

    }

    @Override
    public int getItemCount() {

        return wifiArrayList.size();
    }
    public void addKey(Wifi dataObj, int index) {
        wifiArrayList.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteKey(int index) {
        wifiArrayList.remove(index);
        notifyItemRemoved(index);
    }
    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public CardViewAdapter(ArrayList<Wifi> wifiArrayList) {
        this.wifiArrayList=wifiArrayList;
    }

    public static class KeyHolder extends RecyclerView.ViewHolder
            implements OnClickListener{

        TextView ssid,status,color;
        View v;
        RelativeLayout card_view_main;
        CardView card;

        public KeyHolder(View itemView) {
            super(itemView);

            ssid = (TextView) itemView.findViewById(R.id.label_SSID_in_cardView);
            status = (TextView) itemView.findViewById(R.id.label_status_in_cardView);
            card_view_main=(RelativeLayout)itemView.findViewById(R.id.cardview_main);
            card=(CardView) itemView.findViewById(R.id.card_view);
            v=itemView.findViewById(R.id.view);

            v.setBackgroundResource(R.drawable.wifi_icon);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static ArrayList<Wifi> getWifiArrayList() {
        return wifiArrayList;
    }
}
