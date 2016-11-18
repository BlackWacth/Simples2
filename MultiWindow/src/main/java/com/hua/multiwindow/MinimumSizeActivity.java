package com.hua.multiwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MinimumSizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimum_size);
        setTitle("MinimumSize");
        setDescription(R.id.minimum_size_description, R.string.activity_minimum_description);
    }
}
