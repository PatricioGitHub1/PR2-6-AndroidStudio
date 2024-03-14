package com.patricio.fragments.ui.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.patricio.fragments.MainActivity;
import com.patricio.fragments.R;

public class AccelerometerFrag extends Fragment {

    private SensorManager sensorManager;
    private Sensor sensor;
    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 800;
    private TextView textX;
    private TextView textZ;
    private TextView textY;

    private float last_x, last_y, last_z;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor != null) {
            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i("infoo", "reattached");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accelerometer, container, false);

        textX = view.findViewById(R.id.textX);
        textY = view.findViewById(R.id.textY);
        textZ = view.findViewById(R.id.textZ);

        return view;
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            long curTime = System.currentTimeMillis();

            float xAcc = sensorEvent.values[0];
            float yAcc = sensorEvent.values[1];
            float zAcc = sensorEvent.values[2];

            xAcc = (float) (Math.round(xAcc * 100.0) / 100.0);
            yAcc = (float) (Math.round(yAcc * 100.0) / 100.0);
            zAcc = (float) (Math.round(zAcc * 100.0) / 100.0);

            updateTextViews(xAcc,yAcc, zAcc);

            curTime = System.currentTimeMillis();
            long diffTime = (curTime - lastUpdate);

            if (diffTime > 5.0f) {
                float speed = Math.abs(xAcc + yAcc + zAcc - last_x - last_y - last_z) / diffTime * 10000;
                //Log.i("Infoo", "diff time: "+diffTime);
                lastUpdate = curTime;

                if (speed > SHAKE_THRESHOLD) {
                    Log.i("INFOO", "shake detected w/ speed "+speed);
                    toastShake();
                }
            }
            last_x = xAcc;
            last_y = yAcc;
            last_z = zAcc;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // Not needed for now
        }

        private void updateTextViews(float x, float y, float z) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textX.setText(String.valueOf(x));
                        textZ.setText(String.valueOf(z));
                        textY.setText(String.valueOf(y));
                    }
                });
            } else {
                Log.w("warning", "getActivity null en updateTextViews");
            }

        }

        private void toastShake() {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireContext(), "Se ha hecho DoubleTap ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w("warning", "getActivity null en toastShake");
            }

        }

    };
}