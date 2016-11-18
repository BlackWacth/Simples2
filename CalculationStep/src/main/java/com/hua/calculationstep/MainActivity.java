package com.hua.calculationstep;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hua.calculationstep.service.StepService;
import com.hua.calculationstep.widgets.StepArcView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StepArcView stepArcView = (StepArcView) findViewById(R.id.step_arc_view);
        if(isSupportStepCountSensor(this)) {
            Intent intent = new Intent(this, StepService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "不支持计步", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否支持计步
     * @param context
     * @return
     */
    public boolean isSupportStepCountSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        return stepCountSensor != null || stepDetectorSensor != null;
    }
}
