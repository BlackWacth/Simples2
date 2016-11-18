
package com.hua.calculationstep.service;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import com.hua.calculationstep.utils.L;

public class StepService extends Service implements SensorEventListener{

    private SensorManager mSensorManager;
    private static int mStepSensor = -1;
    private boolean hasRecord = false;
    private int hasStepCount = 0;
    private int mCurrentStepCount = 0;
    private int mPrivousStepCount = 0;

    public StepService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startStepSensor();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startStepSensor() {
        if(Build.VERSION.SDK_INT < 19) {
            return ;
        }
        if(mSensorManager != null) {
            mSensorManager = null;
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor stepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor stepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepCounterSensor != null) {
            mStepSensor = 0;
            mSensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if(stepDetectorSensor != null) {
            mStepSensor = 1;
            mSensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        L.i("SensorEvent = " + sensorEvent.toString());
        if(mStepSensor == 0) {
            int tempStep = (int) sensorEvent.values[0];
            if(!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                int thisStepCount = tempStep - hasStepCount;
                mCurrentStepCount += (thisStepCount - mPrivousStepCount);
                mPrivousStepCount = thisStepCount;
            }
        } else if(mStepSensor == 1) {
            if(sensorEvent.values[0] == 1.0) {
                mCurrentStepCount ++;
            }
        }
        L.i("mCurrentStepCount = " + mCurrentStepCount);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
