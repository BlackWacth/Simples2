package com.hua.multiwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UnresizableActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unresizable);
        setTitle("Unresizable");
        setDescription(R.id.unresizable_desc, R.string.activity_description_unresizable);
    }
}
