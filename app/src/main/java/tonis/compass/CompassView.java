package tonis.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CompassView extends View {

    private int degrees;

    private float centerX = 0.0f;
    private float centerY = 0.0f;
    private float textSize = 50f;

    private Paint lineStyle = new Paint();
    private Paint textStyle = new Paint();

    public CompassView(Context context) {
        super(context);
        init();
    }

    private void init() {
        degrees = 0;
        lineStyle.setColor(Color.BLACK);
        lineStyle.setStrokeWidth(12f);
        textStyle.setTextSize(textSize);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initCenter() {
        centerX = (this.getWidth() / 2);
        centerY = (this.getHeight() / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
         double arrowLength = Math.min((canvas.getWidth() / 2) * 0.7, (canvas.getHeight() / 2) * 0.7);

        if (centerX == 0.0F) initCenter();

        double radians = Math.toRadians(degrees);

        Log.i("draw", String.format("radians %.2f", radians));

        canvas.drawLine(X(radians, 50.0), Y(radians, 50.0), X(radians, arrowLength), Y(radians, arrowLength), lineStyle);

        canvas.drawText(toLabel(degrees), X(radians, arrowLength + textSize), Y(radians, arrowLength + textSize), textStyle);
    }

    private String toLabel(int degrees) {
        String[] labels = {"N","NE","E","SE", "S","SW","W","NW"};
        int skewed = (degrees + 22) % 360;
        int index = (Math.round((skewed / 45)) % 8);
        return labels[index];
    }

    private float Y(double radians, double arrowLength) {
        return (float) (centerY - (cos(radians) * arrowLength));
    }


    private float X(double radians, double arrowLength){
        return (float) (centerX - (sin(radians) * arrowLength));
    }

    public void setAzimuth(int azimuth) {
        this.degrees = azimuth;
        invalidate();
    }
}
