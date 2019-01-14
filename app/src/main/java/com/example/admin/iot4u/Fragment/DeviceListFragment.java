package com.example.admin.iot4u.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admin.iot4u.Adapter.DeviceListAdapter;
import com.example.admin.iot4u.Database.DBDeviceInfor;
import com.example.admin.iot4u.Database.DeviceInfor;
import com.example.admin.iot4u.R;
import com.example.admin.iot4u.SwipeController.SwipeController;
import com.example.admin.iot4u.WifiSettings.WiFiSettings;
import com.example.admin.iot4u.WifiSettings.WifiSettingForESP;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragment extends Fragment {
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    List<DeviceInfor> deviceInforList = new ArrayList<>();
    DBDeviceInfor dbDeviceInfor;

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
        dbDeviceInfor = new DBDeviceInfor(view.getContext());
        DBDeviceInfor.getInstance(view.getContext()).addDevice(new DeviceInfor("TEST","TEST MAC","qwerty123456"));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(view.getContext(), "Floating Button", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(view.getContext(), WiFiSettings.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        deviceInforList = dbDeviceInfor.getAllDevice();
        updateRecyclerView();
    }

    private void updateRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(),
                LinearLayoutManager.VERTICAL,false);

        DeviceListAdapter adapter = new DeviceListAdapter(this.getContext(),deviceInforList);


        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(layoutManager);

        //RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this.getContext(),DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(decoration);

        SwipeController swipeController = new SwipeController();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
