package com.tld.company;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tld.company.activity.PlantInfoActivity;
import com.tld.company.recognize.RecognizeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements View.OnClickListener {

    @BindView(R.id.flower)
    Button flowerButton;
    @BindView(R.id.flower2)
    Button flower2Button;
    @BindView(R.id.weed)
    Button weedButton;
    @BindView(R.id.plant_info)
    Button plantInfoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        flowerButton.setOnClickListener(this);
        flower2Button.setOnClickListener(this);
        weedButton.setOnClickListener(this);
        plantInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flower:
                RecognizeActivity.launch(this, "Flower");
                break;
            case R.id.flower2:
                RecognizeActivity.launch(this, "Flower2");
                break;
            case R.id.weed:
                RecognizeActivity.launch(this, "Weeds");
                break;
            case R.id.plant_info:
                PlantInfoActivity.launch(this);
                break;
        }
    }
}
