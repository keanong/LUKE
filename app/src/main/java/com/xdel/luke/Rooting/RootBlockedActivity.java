package com.xdel.luke.Rooting;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xdel.luke.R;

/**
 * Shown when RootGuard has blocked this process (root detected and enforcement on).
 * Closes the app's activities only: no data is deleted and the user is not logged out.
 * Finishes immediately if the process is not blocked, so it is inert while ROOT_ENFORCE=false.
 */
public class RootBlockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RootGuard.isBlocked()) {
            finish();
            return;
        }
        setContentView(R.layout.activity_root_blocked);
        findViewById(R.id.root_blocked_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApp();
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeApp();
    }

    private void closeApp() {
        try {
            finishAffinity();
        } catch (Throwable t) {
            finish();
        }
    }
}
