package com.xdel.luke;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.xdel.luke.objClasses.COD_List_PostData;
import com.xdel.luke.objClasses.COD_List_RecyclerViewAdapter;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class samplehorizontallistview extends AppCompatActivity {

    private RecyclerView listview;
    private List<COD_List_PostData> list = new ArrayList<>();
    private COD_List_RecyclerViewAdapter mAdapter;

    private static String[] dataObjects = new String[]{ "Text #1",
            "Text #2",
            "Text #3","Text #4","Text #5","Text #6","Text #7","Text #8","Text #9","Text #10" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samplehorizontallistview);
    }

    @Override
    protected void onResume(){
        try {
            listview = (RecyclerView) findViewById(R.id.listview);
            for (int i = 0; i <= dataObjects.length-1;i++) {
                COD_List_PostData data = new COD_List_PostData();
                data.setID(dataObjects[i]);
                list.add(data);
            }

            mAdapter = new COD_List_RecyclerViewAdapter(list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            listview.setLayoutManager(mLayoutManager);
            listview.setItemAnimator(new DefaultItemAnimator());
            listview.setAdapter(mAdapter);

            super.onResume();
        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile( "", "", "", "", "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_samplehorizontallistview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
