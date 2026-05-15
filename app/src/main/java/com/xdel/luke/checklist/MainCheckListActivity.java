package com.xdel.luke.checklist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

import com.xdel.luke.R;

public class MainCheckListActivity extends AppCompatActivity {
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_check_list);

        sv = (ScrollView) findViewById(R.id.sv);


    }

}