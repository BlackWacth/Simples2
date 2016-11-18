package com.hua.multiwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AdjacentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjacent);
        setTitle("Adjacent");
        setDescription(R.id.adjacent_description, R.string.activity_adjacent_description);

    }
}
