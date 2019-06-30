package com.alexc.sensordatacollection;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";


    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int counter;

    // data sync example
    private static final String COUNT_KEY = "com.data.key.count";
    private static final String ACC_DATA_KEY = "com.acc.data.key";
    private static final String TIMESTAMP_KEY = "com.data.key.timestamp";

    private static final int COUNT_DATA=1000;
    private DataClient dataClient;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        mTextView.setVerticalScrollBarEnabled(true);

        // Enables Always-on
        setAmbientEnabled();

        Log.d(TAG, "onCreate: Initiaize Sensor Services");
        this.counter = 0;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this,
                accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "onCreate: Registered accelerometer listener");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (this.counter <= COUNT_DATA) {
//            Log.d(TAG, "onSensorChanged: " + this.counter + "===>> X: " + event.values[0] + " Y: " +
//                    event.values[1] + " Z: " + event.values[2]);
            this.counter++;
            sendSensorDataItem(event.values);
            //  increaseCounter();
        }

    }

    private void sendSensorDataItem(final float[] values) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/accData");
        putDataMapReq.getDataMap().putFloatArray(ACC_DATA_KEY, values);
        putDataMapReq.getDataMap().putInt(COUNT_KEY, count++);
        putDataMapReq.getDataMap().putLong(TIMESTAMP_KEY,System.currentTimeMillis()/1000);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
//                        Log.d(TAG, "onSuccess: Sending counter was successful: " + dataItem + " - " + count +
//                                " - " + values[0] + '.' + values[1] + "." + values[2]);
//                        mTextView.append(count + "\n");

                    }
                });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Create a data map and put data in it
    private void increaseCounter() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putInt(COUNT_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(
                new OnSuccessListener<DataItem>() {
                    @Override
                    public void onSuccess(DataItem dataItem) {
                        Log.d(TAG, "onSuccess: Sending counter was successful: " + dataItem + " - " + count);
                        mTextView.append(count + "\n");

                    }
                });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Wearable.getDataClient(this).addListener(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Wearable.getDataClient(this).removeListener(this);
//    }
//
//    @Override
//    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
//
//    }
}
