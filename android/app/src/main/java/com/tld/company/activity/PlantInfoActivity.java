package com.tld.company.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tld.company.R;
import com.tld.company.bean.PlantInfo;
import com.tld.company.http.InvocationError;
import com.tld.company.upload.OnRecognizeListener;
import com.tld.company.upload.RecognizeManager;
import com.tld.company.util.SystemTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;


public class PlantInfoActivity extends Activity{
    @BindView(R.id.title_search_edit)
    EditText searchEdit;
    @BindView(R.id.title_search_clear)
    View searchClear;
    @BindView(R.id.plant_text)
    TextView plant;
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    private String code="CwZ0AVGtMcl5LJom";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        ButterKnife.bind(this);
        searchEdit.setText(code);
        searchEdit.setSelection(code.length());
        plant.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnEditorAction(R.id.title_search_edit)
    boolean onEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }
        return false;
    }

    @OnTextChanged(R.id.title_search_edit)
    void onTextChanged(CharSequence text) {
        if (TextUtils.isEmpty(text.toString().trim())) {
            searchClear.setVisibility(View.GONE);
        } else {
            searchClear.setVisibility(View.VISIBLE);
        }
    }

    void search() {
        String keyword = searchEdit.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        RecognizeManager.getPlantInfo(keyword, new OnRecognizeListener<PlantInfo>() {
            @Override
            public void onSuccess(PlantInfo recognizeResults) {
                plant.setText(recognizeResults.toString());
            }
            @Override
            public void onFail(InvocationError error) {
                plant.setText(error.getMessage());
            }
        });
        SystemTools.hideSoftInputFromWindow(this);
    }

    @OnClick(R.id.title_search_clear)
    void clearSearch() {
        searchEdit.setText("");
    }

    @OnClick(R.id.title_left_iv)
    void back() {
       finish();
    }

    public static void launch(Context ctx) {
        Intent intent=new Intent(ctx, PlantInfoActivity.class);
        ctx.startActivity(intent);

    }
}
