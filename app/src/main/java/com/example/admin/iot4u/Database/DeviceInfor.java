package com.example.admin.iot4u.Database;

import java.util.jar.Attributes;

public class DeviceInfor {

    public String deviceName;
    public String deviceMac;
    public String deviceUdid;
    public int deviceId;


    public DeviceInfor(){}

    public DeviceInfor(String deviceName, String deviceMac, String deviceUdid) {
        this.deviceName = deviceName;
        this.deviceMac = deviceMac;
        this.deviceUdid = deviceUdid;
    }

    public DeviceInfor(String deviceName, String deviceMac, String deviceUdid, int deviceId) {
        this.deviceName = deviceName;
        this.deviceMac = deviceMac;
        this.deviceUdid = deviceUdid;
        this.deviceId = deviceId;
    }

    public String getDeviceUdid() {
        return deviceUdid;
    }

    public void setDeviceUdid(String deviceUdid) {
        this.deviceUdid = deviceUdid;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

}
