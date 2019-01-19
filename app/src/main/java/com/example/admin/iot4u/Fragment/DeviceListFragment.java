package com.example.admin.iot4u.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.iot4u.Adapter.DeviceListAdapter;
import com.example.admin.iot4u.Database.DeviceInforDatabase;
import com.example.admin.iot4u.Database.DeviceInfor;
import com.example.admin.iot4u.MainActivity;
import com.example.admin.iot4u.R;
import com.example.admin.iot4u.SwipeController.SwipeController;
import com.example.admin.iot4u.SwipeController.SwipeControllerActions;
import com.example.admin.iot4u.WifiSettings.WiFiSettingsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeviceListFragment extends Fragment {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private List<DeviceInfor> deviceInforList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        floatingActionButton = view.findViewById(R.id.btnFloating);
        recyclerView = view.findViewById(R.id.recyclerViewDeviceList);
        DeviceInforDatabase.getInstance(view.getContext()).addDevice(new DeviceInfor(UUID.randomUUID().toString(),"TEST MAC","qwerty123456"));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Floating Button", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(view.getContext(), WiFiSettingsActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        deviceInforList =  DeviceInforDatabase.getInstance(this.getContext()).getAllDevice();
        updateRecyclerView();
    }

    // Call update fragment View in MainActivity
    public void refeshData() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity!=null) mainActivity.updateData();
    }

    private void updateRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(),
                LinearLayoutManager.VERTICAL,false);

        final DeviceListAdapter adapter = new DeviceListAdapter(this.getContext(),deviceInforList);


        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(layoutManager);

        //RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this.getContext(),DividerItemDecoration.VERTICAL);

        final SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int postion) {
                final DeviceInfor deviceInfor = adapter.getDeviceIinfor(postion);
                Toast.makeText(getContext(), "EDIT", Toast.LENGTH_SHORT).show();
                final Dialog editNameDialog = new Dialog(getContext());
                editNameDialog.setContentView(R.layout.dialog_edit_device_name);

                final TextView edtInputName = editNameDialog.findViewById(R.id.edtDialogInputName);
                Button btnOK = editNameDialog.findViewById(R.id.btnDialogOK);
                Button btnCancel = editNameDialog.findViewById(R.id.btnDialogCancel);

                edtInputName.setText(deviceInfor.deviceName.toString());

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = edtInputName.getText().toString();
                        DeviceInforDatabase.getInstance(getContext()).UpdateName(deviceInfor,name);
                        refeshData();
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
            }

            @Override
            public void onRightClicked(int postion) {

                final DeviceInfor deviceInfor = adapter.getDeviceIinfor(postion);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Do you want to delete this device");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Delete Item YES", Toast.LENGTH_SHORT).show();
                        DeviceInforDatabase.getInstance(getContext()).deleteDevice(deviceInfor);
                        refeshData();
                        dialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DO SOMETHING HERE
                        Toast.makeText(getContext(), "Delete Item NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                swipeController.onDraw(c);
            }
        });


    }
}
