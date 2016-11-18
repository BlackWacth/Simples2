package com.hua.multiwindow;

import android.os.Bundle;

public class BasicMultiWindowActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_multi_window);
        setTitle("BasicMultiWindow");
        setDescription(R.id.basic_description, R.string.activity_description_basic);
    }
}
