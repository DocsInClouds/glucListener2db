package com.docsinclouds.glucose2db.G5Utils;

import android.provider.BaseColumns;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.docsinclouds.glucose2db.G5Model.Sensor;
import java.util.List;

/**
 * Created by Emma Black on 11/7/14.
 */
@Table(name = "SensorSendQueue", id = BaseColumns._ID)
public class SensorSendQueue extends Model {

    @Column(name = "Sensor", index = true)
    public Sensor sensor;

    @Column(name = "success", index = true)
    public boolean success;


    public static SensorSendQueue nextSensorJob() {
        SensorSendQueue job = new Select()
                .from(SensorSendQueue.class)
                .where("success =", false)
                .orderBy("_ID desc")
                .limit(1)
                .executeSingle();
        return job;
    }

    public static List<SensorSendQueue> queue() {
        return new Select()
                .from(SensorSendQueue.class)
                .where("success = ?", false)
                .orderBy("_ID desc")
                .execute();
    }

    public static void addToQueue(Sensor sensor) {
        //SendToFollower(sensor); //DIC
        SensorSendQueue sensorSendQueue = new SensorSendQueue();
        sensorSendQueue.sensor = sensor;
        sensorSendQueue.success = false;
        sensorSendQueue.save();
    }

    //DIC
//    public static void SendToFollower(Sensor sensor) {
//       // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(xdrip.getAppContext());
//        if(Home.get_master()) {
//            GcmActivity.syncSensor(sensor, true);
//        }
//    }
}
