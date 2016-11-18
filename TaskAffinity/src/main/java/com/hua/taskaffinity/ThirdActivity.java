package com.hua.taskaffinity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ThirdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setUi();
    }

    public void goToNext(View view){
//        mStartActivity(ThirdActivity.class);
    }
}
