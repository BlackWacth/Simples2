package com.hua.taskaffinity2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUi();
    }

    public void goToNext(View view){
        Intent intent = new Intent(this, FourthActivity.class);
        startActivity(intent);
    }
}
