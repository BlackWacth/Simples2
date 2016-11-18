package com.bruce.transition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

public class BasicTransitionActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewGroup mRootView;
    private View mRedBox, mGreenBox, mBlueBox, mBlackBox;

    private RadioGroup mRadioGroup;
    private Transition mTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_transition);

        mRootView = (ViewGroup) findViewById(R.id.activity_basic_transition);
        mRootView.setOnClickListener(this);

        mRedBox = findViewById(R.id.red_box);
        mGreenBox = findViewById(R.id.green_box);
        mBlueBox = findViewById(R.id.blue_box);
        mBlackBox = findViewById(R.id.black_box);

        mRadioGroup = (RadioGroup) findViewById(R.id.transition_type);
        mRadioGroup.check(R.id.transition_explode);
        mTransition = new Explode();

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.transition_explode:
                        mTransition = new Explode();
                        break;

                    case R.id.transition_fade:
                        mTransition = new Fade();
                        break;

                    case R.id.transition_slide:
                        mTransition = new Slide();
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        TransitionManager.beginDelayedTransition(mRootView, mTransition);
        toggleVisibility(mRedBox, mGreenBox, mBlueBox,mBlackBox);
    }

    private void toggleVisibility(View... views){
        for(View view : views) {
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
