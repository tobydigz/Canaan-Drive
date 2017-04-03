package xyz.digzdigital.canaandrive.ui.user;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

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

import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.data.model.Driver;

public class TripActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private String driverId;
    private DatabaseReference reference;
    private Driver driver;
    private Marker driverMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (getIntent().getExtras() != null){
            driverId = getIntent().getStringExtra("driverId");
        }

        reference = FirebaseDatabase.getInstance().getReference();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        driverMarker = createMarker(new LatLng(6.2, 3.9));

        loadDriverLocation();
    }

    private void loadDriverLocation() {
        reference.child("drivers").child(driverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                driver = new Driver();
                driver.setName((String) snapshot.child("name").getValue());
                driver.setCar((String) snapshot.child("car").getValue());
                driver.setRating((Float) snapshot.child("rating").getValue());
                driver.setLatitude((Double) snapshot.child("latitude").getValue());
                driver.setLongitude((Double) snapshot.child("longitude").getValue());
                driverMarker.setPosition(new LatLng(driver.getLatitude(), driver.getLongitude()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Marker createMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Driver Location");
        return map.addMarker(options);
    }
}
