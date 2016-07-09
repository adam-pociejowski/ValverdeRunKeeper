package com.example.valverde.valverderunkeeper.main_menu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.valverde.valverderunkeeper.R;
import com.example.valverde.valverderunkeeper.data.DatabaseResult;
import com.example.valverde.valverderunkeeper.running.TrackerActivity;
import com.example.valverde.valverderunkeeper.running.processing_result.RunResult;
import com.example.valverde.valverderunkeeper.statistics.ResultsPresentationActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainMenuActivity extends AppCompatActivity {
    @BindView(R.id.goRunningButton) Button goRunningButton;
    @BindView(R.id.statisticsButton) Button statisticsButton;
    @BindView(R.id.setingsButton) Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        ButterKnife.bind(this);

        goRunningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrackerActivity.class);
                startActivity(intent);
            }
        });

        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResultsPresentationActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}