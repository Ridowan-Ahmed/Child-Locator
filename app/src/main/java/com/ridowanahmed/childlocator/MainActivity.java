package com.ridowanahmed.childlocator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ridowanahmed.childlocator.Dashboard.ChildDashboard;
import com.ridowanahmed.childlocator.Dashboard.ParentDashboard;
import com.ridowanahmed.childlocator.Login.ChildLoginActivity;
import com.ridowanahmed.childlocator.Login.ParentLoginActivity;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth;
    private AlertDialog alertDialog;
    private TextView textView_loginOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        Log.e(TAG, "Auth " + mFirebaseAuth.toString());
        textView_loginOptions = (TextView) findViewById(R.id.textView_loginOptions);
        Typeface lobster = Typeface.createFromAsset(getAssets(), "Lobster-Regular.ttf");
        textView_loginOptions.setTypeface(lobster);

        //alert dialog message for internet
        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Internet Connection Error !");
        alertDialog.setMessage("Please, Check Your Internet Connection.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

    }
    public void parentLoginBtn(View view){
        if(!isConnected()){
            alertDialog.show();
            return;
        }
        SharedPreferences mSharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        final String phoneNumber = mSharedPreferences.getString(getString(R.string.PARENT_GIVE_NUMBER), "");
        if(!TextUtils.isEmpty(phoneNumber)) {
            startActivity(new Intent(MainActivity.this,ParentDashboard.class));
            return;
        } else if (mFirebaseAuth.getCurrentUser() != null){
            mFirebaseAuth.signOut();
            Log.e(TAG, "Starting ParenLoginActivity");
        }
        startActivity(new Intent(MainActivity.this,ParentLoginActivity.class));
    }

    public void childLoginBtn(View view){
        if(!isConnected()){
            alertDialog.show();
            return;
        }
        SharedPreferences mSharedPreferences = MainActivity.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        final String phoneNumber = mSharedPreferences.getString(getString(R.string.CHILD_GIVE_NUMBER), "");
        if(!TextUtils.isEmpty(phoneNumber)) {
            startActivity(new Intent(MainActivity.this,ChildDashboard.class));
            return;
        } else if (mFirebaseAuth.getCurrentUser() != null){
            mFirebaseAuth.signOut();
            Log.e(TAG, "Starting ChildLoginActivity");
        }
        startActivity(new Intent(MainActivity.this,ChildLoginActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    protected boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean flag = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return flag;
    }

    @Override
    public void onBackPressed() {
        final AlertDialog exitDialog = new AlertDialog.Builder(MainActivity.this).create();
        exitDialog.setTitle("EXIT CHILD LOCATOR ?");
        exitDialog.setMessage("Are you sure you want to exit ChiLdLocator ?");
        exitDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        exitDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        exitDialog.dismiss();
                        return;
                    }
                });
        exitDialog.show();
        //super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
