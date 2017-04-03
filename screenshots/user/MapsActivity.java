package xyz.digzdigital.canaandrive.ui.user;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.ButterKnife;
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.data.DriversLoader;
import xyz.digzdigital.canaandrive.data.model.Driver;
import xyz.digzdigital.canaandrive.location.LocationHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationHelper.LocationHelperListener, DriversLoader.DriversLoaderListener {

    private GoogleMap map;
    private LocationHelper locationHelper;
    private DriversLoader driversLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        locationHelper = new LocationHelper(this);
        locationHelper.setListener(this);
        locationHelper.createGoogleClient();

        driversLoader = new DriversLoader();
        driversLoader.setListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.onStop();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLastLocationGotten(Location location) {

    }

    @Override
    public void onLocationChanged(LatLng latLng) {
        driversLoader.setLatLng(latLng);
        driversLoader.loadDrivers();
    }

    @Override
    public void onDriversLoaded(ArrayList<Driver> drivers) {
map.clear();
        for (Driver driver : drivers) {
            createMarker(driver);
        }

    }

    private void createMarker(Driver driver) {
        Marker marker;
    }
}
