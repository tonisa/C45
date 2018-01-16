package tonis.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    CompassView compass;
    TextView azimuthLabel;

    SensorManager sensorManager;
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];

    int lastAzimuth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compass = (CompassView) this.findViewById(R.id.compass);
        azimuthLabel = (TextView)this.findViewById(R.id.azimuth);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, mGravity, 0, event.values.length);
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values, 0, mGeomagnetic, 0, event.values.length);
        }

        if(mGravity !=null && mGeomagnetic !=null){
            float [] R = new float[9];
            float [] I = new float [9];

            boolean success = sensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if(success){
                float [] orientation = new float [3];
                sensorManager.getOrientation(R, orientation);
                float azimuthInRadians = orientation[0];
                int azimuth = (int) (Math.toDegrees(azimuthInRadians) + 360) % 360;


                if (azimuth == lastAzimuth) return;

                lastAzimuth = azimuth;
                Log.i("compass", String.format("Azimuth is %d deg, %.2f rad, %.2f rawdeg", azimuth, azimuthInRadians, Math.toDegrees(azimuthInRadians)));

                updateAzimuth(azimuth);
            }
            else
                Log.i("compass", "getRotationMatrix failed");
        }
    }

    private void updateAzimuth(int azimuth) {
        compass.setAzimuth(azimuth);
        azimuthLabel.setText("asimuut " + azimuthLabel(azimuth));
    }

    private String azimuthLabel(int degrees) {
        String[] labels = {"N","NE","E","SE", "S","SW","W","NW"};
        int skewed = (degrees + 22) % 360;
        int index = (Math.round((skewed / 45)) % 8);
        return labels[index];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
