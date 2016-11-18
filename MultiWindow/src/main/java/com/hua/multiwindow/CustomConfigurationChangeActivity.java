package com.hua.multiwindow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomConfigurationChangeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_configuration_change);

        setTitle("CustomConfigurationChange");
        setDescription(R.id.custom_configuration_description, R.string.activity_custom_description);
    }
}
