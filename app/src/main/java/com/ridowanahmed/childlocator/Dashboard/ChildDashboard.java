package com.ridowanahmed.childlocator.Dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ridowanahmed.childlocator.Map.ChildMap;
import com.ridowanahmed.childlocator.R;
import com.ridowanahmed.childlocator.Services.MAP_Service;

public class ChildDashboard extends AppCompatActivity {
    private static final int REQ_CODE = 1410649642 ;
    private final String TAG = "ChildDashboard";
    private Button button_showLocation;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);

        SharedPreferences mSharedPreferences = ChildDashboard.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        phoneNumber = mSharedPreferences.getString(getString(R.string.CHILD_GIVE_NUMBER), "");
        Log.e(TAG, "Phone is " + phoneNumber);

        if(!runtime_permissions()) {
            enableService();
        }

        button_showLocation = (Button) findViewById(R.id.button_showLocation);
        button_showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChildDashboard.this, ChildMap.class));
            }
        });

    }

    private void enableService() {
        startService(new Intent(ChildDashboard.this, MAP_Service.class));
        Log.e(TAG, "Service Started");
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQ_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                enableService();
            } else {
                runtime_permissions();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Destroyed " + phoneNumber);
    }
}
