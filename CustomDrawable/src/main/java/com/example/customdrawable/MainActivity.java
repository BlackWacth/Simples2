package com.example.customdrawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.customdrawable.drawable.CircleImageDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleImageDrawable drawable = new CircleImageDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.timg));
        ((ImageView)findViewById(R.id.icon)).setImageDrawable(drawable);
    }
}
