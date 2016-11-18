package com.bruce.transition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBasicTransition(View view) {
        startActivity(BasicTransitionActivity.class);
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
