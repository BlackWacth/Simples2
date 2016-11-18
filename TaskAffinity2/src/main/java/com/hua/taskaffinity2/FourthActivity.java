package com.hua.taskaffinity2;

import android.os.Bundle;
import android.view.View;

public class FourthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        setUi();
    }

    public void goToNext(View view){
//        mStartActivity(FourthActivity.class);
    }
}
