package com.campusconnect.activity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.campusconnect.R;
import com.squareup.picasso.Picasso;

public class ZoomPictureActivity extends AppCompatActivity {
    String url;ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    ImageView p_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_picture);

         p_image = (ImageView) findViewById(R.id.picture);

        try {
            url = getIntent().getStringExtra("PICTURE");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (url.isEmpty() || url != null) {
            Picasso.with(ZoomPictureActivity.this).load(url).into(p_image);
        }
        else{
            Picasso.with(ZoomPictureActivity.this).load(R.mipmap.spark_session).into(p_image);
        }
        scaleGestureDetector = new ScaleGestureDetector(this,new ScaleListener());
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            p_image.setImageMatrix(matrix);
            return true;
        }
    }
}
