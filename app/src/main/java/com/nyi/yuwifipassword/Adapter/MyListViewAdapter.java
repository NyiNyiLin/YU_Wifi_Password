package com.nyi.yuwifipassword.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nyi.yuwifipassword.R;
import com.nyi.yuwifipassword.data.Wifi;
import com.nyi.yuwifipassword.utils.Constants;

import java.util.List;

/**
 * Created by IN-3442 on 26-Apr-16.
 */
public class MyListViewAdapter extends ArrayAdapter<Wifi> {

    private Context context;
    private List<Wifi> wifiList;

    public MyListViewAdapter(Context context, int resource, List<Wifi> objects) {
        super(context, resource, objects);
        this.context = context;
        this.wifiList = objects;
    }


    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView SSID;
        public TextView status;
        public View imageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_view_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.SSID = (TextView) rowView.findViewById(R.id.label_SSID_in_listVieww);
            viewHolder.status = (TextView) rowView.findViewById(R.id.label_status_in_listView);
            viewHolder.imageView = (View) rowView.findViewById(R.id.view_in_listView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        Wifi wifi = wifiList.get(position);
        viewHolder.SSID.setText(wifi.getSsid());
        viewHolder.status.setText(wifi.getStatus());

        if(wifi.getStatus().compareTo(Constants.AVAILABLE)==0){
            viewHolder.imageView.setBackgroundResource(R.drawable.wifi_available);
        }else{
            viewHolder.imageView.setBackgroundResource(R.drawable.not_in_range);
        }

        return rowView;
    }
}
