package com.example.admin.iot4u.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.iot4u.Database.DeviceInforDatabase;
import com.example.admin.iot4u.MQTT.ControlDevicePubSub;
import com.example.admin.iot4u.MQTT.ControlDevicePubSubActivity;
import com.example.admin.iot4u.Database.DeviceInfor;
import com.example.admin.iot4u.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder>{

    public Context rContext;
    public List<DeviceInfor> deviceInfors;
    public DeviceInfor deviceInfor;

    public DeviceListAdapter(Context rContext, List<DeviceInfor> deviceInfors) {
        this.rContext = rContext;
        this.deviceInfors = deviceInfors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.recyclerview_item,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(postView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DeviceInfor deviceInfor = deviceInfors.get(i);
        viewHolder.tvDeviceName.setText(deviceInfor.deviceName.toString());
        viewHolder.tvDeviceMac.setText(String.valueOf(deviceInfor.deviceId));
        final ControlDevicePubSub pubSubAWS = new ControlDevicePubSub(rContext,deviceInfor.deviceUdid);
        pubSubAWS.initialAWS();
        viewHolder.swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true) {

                    String pubMsgOn = "{\"FUNCTION\":\"ON\"}";
                    pubSubAWS.publishAWS(pubMsgOn);
                } else {

                    String pubMsgOff = "{\"FUNCTION\":\"OFF\"}";
                    pubSubAWS.publishAWS(pubMsgOff);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceInfors.size();
    }

    public DeviceInfor getDeviceIinfor(int adapterPosition) {
        return deviceInfors.get(adapterPosition);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvDeviceName;
        public TextView tvDeviceMac;
        public Switch swOnOff;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceMac  = itemView.findViewById(R.id.tvDeviceMAC);
            swOnOff = itemView.findViewById(R.id.sw_on_off_item);
            tvDeviceName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            deviceInfor = getDeviceIinfor(getAdapterPosition());
            switch (v.getId()){
                case R.id.tvDeviceName:
                    Toast.makeText(v.getContext(), "ViewHolder Item", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), ControlDevicePubSubActivity.class);
                    intent.putExtra("Name", deviceInfor.deviceName.toString());
                    intent.putExtra("Mac",deviceInfor.deviceMac.toString());
                    intent.putExtra("UDID",deviceInfor.getDeviceUdid());
                    v.getContext().startActivity(intent);
                    break;
            }
        }
    }
}
