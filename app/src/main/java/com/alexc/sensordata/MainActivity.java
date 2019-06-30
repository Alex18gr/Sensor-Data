package com.alexc.sensordata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

    private static final String COUNT_KEY = "com.data.key.count";
    private static final String ACC_DATA_KEY = "com.acc.data.key";
    private static final String TIMESTAMP_KEY = "com.data.key.timestamp";

    private int count = 0;
    private List<float[]> valuesList = new ArrayList<>();
    private List<String> stringValuesString = new ArrayList<>();

    private static final String TAG = "MainActivityApp";
    private TextView txtCounter;
    private TextView txtValue;
    private TextView txtList;
    private TextView txtViewList;

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWidgets();
        Log.d(TAG, "onCreate: statrtup the application.... !!!!");
        // Wearable.getDataClient(this).addListener(this);
    }

    private void setupWidgets() {
        txtCounter = findViewById(R.id.textCounterNum);
        txtCounter.setText("0");
        txtValue = findViewById(R.id.textValueNum);
        txtValue.setText("0");
        txtList = findViewById(R.id.txtListValue);
        txtViewList = findViewById(R.id.txtViewList);
        txtViewList.setMovementMethod(new ScrollingMovementMethod());
        Button myBtn = findViewById(R.id.btnListLen);
        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtList.setText(String.valueOf(valuesList.size()));
                txtViewList.setText("");
                for(String value : stringValuesString)
                    txtViewList.append(value + "\n");
            }
        });
    }

    private void setTextViewValues(int counter, float[] accValues) {
        this.txtCounter.setText(String.valueOf(counter) + "/" + String.valueOf(this.count));
        this.txtValue.setText("X: " + String.valueOf(accValues[0]) + " Y: " +
                String.valueOf(accValues[1]) + " Z: " + String.valueOf(accValues[2]));
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        // Log.d(TAG, "onDataChanged: data changed!!!");
        // Log.d(TAG, "onDataChanged: " + dataEventBuffer);
        for (DataEvent event : dataEventBuffer) {
            Log.d(TAG, "onDataChanged: " + event);
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Data Item changed!!!
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/accData") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    this.count++;
                    float[] values = dataMap.getFloatArray(ACC_DATA_KEY);
                    int receivedCounter = dataMap.getInt(COUNT_KEY);
                    long timestamp = dataMap.getLong(TIMESTAMP_KEY);
                    this.valuesList.add(values);
                    this.stringValuesString.add(String.valueOf(receivedCounter) + "," + String.valueOf(values[0])+","
                            + String.valueOf(values[1])+"," + String.valueOf(values[2])+  "," + String.valueOf(timestamp));
                    Log.d(TAG, "onDataChanged: " + receivedCounter + " , Acc Data: " +
                            values[0] + '.' + values[1] + "." + values[2]);
                    setTextViewValues(receivedCounter, values);
                }
            }
        }
    }
}
