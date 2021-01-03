package com.example.hunting_cloud_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class LovneDobe extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lovnedobe);

        ImageView MenuBtn = (ImageView) findViewById(R.id.meni);

        ImageView ObvBtn = (ImageView) findViewById(R.id.Obvestilo);

        MenuBtn.setOnClickListener(this);
        ObvBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meni:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.Obvestilo:
                break;
        }
    }
}