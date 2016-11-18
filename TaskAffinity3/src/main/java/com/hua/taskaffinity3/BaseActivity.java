package com.hua.taskaffinity3;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * 基类
 * Created by hzw on 2016/11/2.
 */
public abstract class BaseActivity extends AppCompatActivity{

    protected String tag = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "onCreate");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(tag, "onPostCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(tag, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(tag, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag, "onResume" + ", TaskId = " + getTaskId() + ", hashCode = " + hashCode());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(tag, "onPostResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(tag, "onConfigurationChanged: " + newConfig.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(tag, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(tag, "onRestoreInstanceState");
    }

    public void mStartActivity(Class<?> cls, boolean hasFlags, int flags) {
        Intent intent = new Intent(this, cls);
        if(hasFlags) {
            intent.setFlags(flags);
        }
        startActivity(intent);
    }

    public void mStartActivity(Class<?> cls) {
        mStartActivity(cls, false, 0);
    }

    public void setDesc(int viewId, int textId) {
        TextView textView = (TextView) findViewById(viewId);
        if(textView != null) {
            textView.setText(textId);
        }
    }

    public void setDesc(int viewId, String text) {
        TextView textView = (TextView) findViewById(viewId);
        if(textView != null) {
            textView.setText(text);
        }
    }

    public void setUi() {
        setTitle(tag + "3");
        setDesc(R.id.desc, "TaskAffinity 3: " + tag);
    }
}
