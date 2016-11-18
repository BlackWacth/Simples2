package com.hua.appshortcuts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ThirdlyActivity extends AppCompatActivity {

    public static final String ACTION = "com.hua.appshortcuts.MULTI_INTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thirdly);
    }
}
