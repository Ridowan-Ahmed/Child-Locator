package com.ridowanahmed.childlocator.Map;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ridowanahmed.childlocator.Dashboard.ParentDashboard;
import com.ridowanahmed.childlocator.Model.ChildInformation;
import com.ridowanahmed.childlocator.R;

/**
 * Created by Ridowan Ahmed on 0016, August, 16, 2017.
 */

public class ParentMap extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    SupportMapFragment mMapFrag;
    private DatabaseReference childData;
    Marker mMarker;
    private final String TAG = "ParentMap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.fragment_map);

//        getSupportActionBar().setTitle("Child Location Activity");
        mMapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFrag.getMapAsync(this);

        SharedPreferences mSharedPreferences = ParentMap.this.getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
        final String phoneNumber = mSharedPreferences.getString(getString(R.string.PARENT_GIVE_NUMBER), "");
        final String childName = getIntent().getExtras().getString(ParentDashboard.CHILD_NAME);
        Log.e(TAG, childName + " " + phoneNumber);
        childData = FirebaseDatabase.getInstance().getReference(phoneNumber).child(childName);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        childData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChildInformation childInfo = dataSnapshot.getValue(ChildInformation.class);
                showOnMap(childInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showOnMap(ChildInformation mChildInformation) {
        if(mMap ==  null){
            Log.e(TAG, "Map is null");
            return;
        } else if(mMarker != null) {
            mMarker.remove();
        }

        LatLng latLng = new LatLng(mChildInformation.getLatitude(), mChildInformation.getLongitude());

        Log.e("Latitude " + mChildInformation.getLatitude() , "Longitude " + mChildInformation.getLongitude());

        String title = mChildInformation.getChildName();
        MarkerOptions locationMarker = new MarkerOptions().position(latLng).title(title);
        locationMarker.snippet(mChildInformation.getTimeString());
        mMarker = mMap.addMarker(locationMarker);
        mMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

        Toast toast = Toast.makeText(getApplicationContext(), "Locating " + title, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 30);
        toast.show();
    }
}
