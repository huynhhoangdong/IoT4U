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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.iot4u.Database.DeviceInforDatabase;
import com.example.admin.iot4u.MQTT.ControlDevicePubSubActivity;
import com.example.admin.iot4u.Database.DeviceInfor;
import com.example.admin.iot4u.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder>{

    public Context rContext;
    public List<DeviceInfor> deviceInfors;
    DeviceInfor deviceInfor;
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
        viewHolder.tvDeviceMac.setText(deviceInfor.deviceMac.toString());

    }

    @Override
    public int getItemCount() {
        return deviceInfors.size();
    }

    public DeviceInfor getDeviceIinfor(int adapterPosition) {
        return deviceInfors.get(adapterPosition);

    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvDeviceName;
        public TextView tvDeviceMac;
        public ImageView deleteItem;
        public ImageView editItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvDeviceMac  = itemView.findViewById(R.id.tvDeviceMAC);
            deleteItem   = itemView.findViewById(R.id.btn_delete_item);
            editItem     = itemView.findViewById(R.id.btn_edit_item);
            tvDeviceName.setOnClickListener(this);
            deleteItem.setOnClickListener(this);
            editItem.setOnClickListener(this);

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

                case R.id.btn_delete_item:
                    Toast.makeText(rContext, "Delete Item", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(rContext);
                    alertDialog.setTitle("Delete");
                    alertDialog.setMessage("Do you want to delete this device");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(rContext, "Delete Item YES", Toast.LENGTH_SHORT).show();
                            DeviceInforDatabase.getInstance(rContext).deleteDevice(deviceInfor);
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO SOMETHING HERE
                            Toast.makeText(rContext, "Delete Item NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                    break;
                case R.id.btn_edit_item:
                    Toast.makeText(rContext, "Edit Item", Toast.LENGTH_SHORT).show();
                    final Dialog editNameDialog = new Dialog(rContext);
                    editNameDialog.setContentView(R.layout.dialog_edit_device_name);

                    final TextView edtInputName = editNameDialog.findViewById(R.id.edtDialogInputName);
                    Button btnOK = editNameDialog.findViewById(R.id.btnDialogOK);
                    Button btnCancel = editNameDialog.findViewById(R.id.btnDialogCancel);

                    edtInputName.setText(deviceInfor.deviceName.toString());

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = edtInputName.getText().toString();
                            DeviceInforDatabase.getInstance(rContext).UpdateName(deviceInfor,name);
                            editNameDialog.dismiss();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editNameDialog.cancel();
                        }
                    });

                    editNameDialog.show();

                    break;

            }


        }
    }
}
