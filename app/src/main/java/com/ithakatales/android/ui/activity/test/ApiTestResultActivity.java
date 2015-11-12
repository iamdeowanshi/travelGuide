package com.ithakatales.android.ui.activity.test;

import android.os.Bundle;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseActivity;

import butterknife.Bind;

public class ApiTestResultActivity extends BaseActivity {

    @Bind(R.id.txt_response) TextView txtResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_api_result);

        Bundle bundle = getIntent().getExtras();
        String response = bundle.getString("response");
        txtResponse.setText(response);
    }

}
