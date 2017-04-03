package xyz.digzdigital.canaandrive.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import xyz.digzdigital.canaandrive.adapter.PlaceAutoCompleteAdapter;

/**
 * Created by Digz on 28/03/2017.
 */

public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context context;



    private GoogleApiClient googleApiClient;
    private LatLng latLng;
    private LocationHelperListener listener;
    private boolean requestingLocationUpdates;
    private PlaceAutoCompleteAdapter adapter;
    private static final LatLngBounds BOUNDS_NIGERIA = new LatLngBounds(new LatLng(5.065341647205726, 2.9987719580531),
            new LatLng(9.9, 5.9));

    public LocationHelper(Context context) {
        this.context = context;
    }

    public void setListener(LocationHelperListener listener) {
        this.listener = listener;
    }

    public void createGoogleClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void onStart() {
        googleApiClient.connect();
    }

    public void onStop() {
        requestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
    }

    public void onResume() {
        if (shouldResume()) startLocationUpdates();
    }

    private boolean shouldResume() {
        return googleApiClient.isConnected() && !requestingLocationUpdates;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastLocation != null) {
            // latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            listener.onLastLocationGotten(lastLocation);
            startLocationUpdates();
        }

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        requestingLocationUpdates = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, getLocationRequest(), this);
    }

    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(20000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    @Override
    public void onConnectionSuspended(int i) {
        listener.onConnectionSuspended(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        listener.onConnectionFailed(connectionResult);
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        listener.onLocationChanged(latLng);
    }

    public PlaceAutoCompleteAdapter setUpPlaceAutoCompleteAdapter() {
        return new PlaceAutoCompleteAdapter(context, android.R.layout.simple_list_item_1, googleApiClient, BOUNDS_NIGERIA, null);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public interface LocationHelperListener {
        void onConnectionSuspended(int i);

        void onConnectionFailed(@NonNull ConnectionResult connectionResult);

        void onLastLocationGotten(Location location);

        void onLocationChanged(LatLng latLng);

    }
}
