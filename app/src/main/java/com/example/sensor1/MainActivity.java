package com.example.sensor1;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sensor1.datalogger.DataLoggerManager;

public class MainActivity extends AppCompatActivity {
    TextView textX, textY, textZ;
    SensorManager sensorManager;
    Sensor sensor;

    DataLoggerManager dataLogger;
    boolean logData = false;
    Button btn_start, btn_stop;

    private final static int WRITE_EXTERNAL_STORAGE_REQUEST = 1000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        textX = findViewById(R.id.textX);
        textY = findViewById(R.id.textY);
        textZ = findViewById(R.id.textZ);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        dataLogger = new DataLoggerManager(this);
        button_init();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroListener);
    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            textX.setText("X : " + (int) x + " rad/s");
            textY.setText("Y : " + (int) y + " rad/s");
            textZ.setText("Z : " + (int) z + " rad/s");

            updateValues(event.values);
        }

    };

    private void button_init() {
        btn_start.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        startDataLog();
                    }
                }

        );
        btn_stop.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        stopDataLog();
                    }
                }
        );
    }

   private void startDataLog() {
       if (!logData) {
           logData = true;
           dataLogger.startDataLog();
       }
   }

   private void stopDataLog() {
        if (logData) {
            logData = false;
            String path = dataLogger.stopDataLog();
            Toast.makeText(this, "File written to " + path, Toast.LENGTH_SHORT).show();
        }
   }

   private void updateValues(float[] values) {
        if (logData) {
            dataLogger.setRotation(values);
        }
    }

    private boolean requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST);
            return false;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

                startDataLog();
            }
        }
    }

}