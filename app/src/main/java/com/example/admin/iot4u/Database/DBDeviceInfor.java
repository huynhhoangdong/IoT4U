package com.example.admin.iot4u.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBDeviceInfor extends SQLiteOpenHelper {

    public static final  String DATABASE_NAME ="DeviceInfor";
    private static final String TABLE_NAME ="Device";
    private static final String ID ="id";
    private static final String NAME ="name";
    private static final String MAC ="mac";
    private static final String UDID ="udid";

    private Context context;

    public DBDeviceInfor(Context context) {
        super(context, DATABASE_NAME,null, 1);
        Log.d("DBManager", "DBManager: ");
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE "+TABLE_NAME +" (" +
                ID   +" integer primary key AUTOINCREMENT, "+
                NAME +" TEXT, "+
                MAC  +" TEXT, "+
                UDID +" TEXT)";
        db.execSQL(sqlQuery);
        Toast.makeText(context, "Create successfylly", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Toast.makeText(context, "Drop successfylly", Toast.LENGTH_SHORT).show();
    }

    /*
    Add new a device
    */

    public void addDevice(DeviceInfor device){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, device.getDeviceName());
        values.put(MAC, device.getDeviceMac());
        values.put(UDID, device.getDeviceUdid());

        db.insert(TABLE_NAME,null,values);
        db.close();

        Toast.makeText(context, "Add new device: " + device.getDeviceMac(), Toast.LENGTH_SHORT).show();
    }

     /*
     Select a device by ID
     */

    public DeviceInfor getDeviceById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        NAME, MAC, UDID}, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        DeviceInfor device = new DeviceInfor(cursor.getString(1),cursor.getString(2),cursor.getString(3));
        cursor.close();
        db.close();
        return device;
    }

     /*
     Update name of device
     */

    public int Update(DeviceInfor device){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME,device.getDeviceName());
        return db.update(TABLE_NAME,values,ID +"=?",new String[] { String.valueOf(device.getDeviceId())});

    }

     /*
     Getting All Device
     */

    public List<DeviceInfor> getAllDevice() {
        List<DeviceInfor> listDevice = new ArrayList<DeviceInfor>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DeviceInfor device = new DeviceInfor();
                device.setDeviceId(cursor.getInt(0));
                device.setDeviceName(cursor.getString(1));
                device.setDeviceMac(cursor.getString(2));
                device.setDeviceUdid(cursor.getString(3));
                listDevice.add(device);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listDevice;
    }

     /*
     Delete a student by ID
     */

    public void deleteDevice(DeviceInfor device) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID + " = ?",
                new String[] { String.valueOf(device.getDeviceId()) });
        db.close();
    }

     /*
     Get Count Student in Table Student
     */

    public int getDeviceCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
