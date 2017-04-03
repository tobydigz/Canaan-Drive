package xyz.digzdigital.canaandrive.ui.user;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.adapter.PlaceAutoCompleteAdapter;
import xyz.digzdigital.canaandrive.data.DriversLoader;
import xyz.digzdigital.canaandrive.data.model.Driver;
import xyz.digzdigital.canaandrive.data.model.Trip;
import xyz.digzdigital.canaandrive.location.LocationHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationHelper.LocationHelperListener, DriversLoader.DriversLoaderListener, RadioGroup.OnCheckedChangeListener, GoogleMap.OnMarkerClickListener {

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();
    private static final LatLngBounds BOUNDS_NIGERIA = new LatLngBounds(new LatLng(5.065341647205726, 2.9987719580531),
            new LatLng(9.9, 5.9));
    private static final int[] COLORS = new int[]{
            R.color.colorPrimaryDark,
            R.color.colorPrimary,
            R.color.colorAccent
    };

    @BindView(R.id.tripDirection)
    RadioGroup tripDirection;
    @BindView(R.id.autoCompleteCardview)
    CardView cardView;
    @BindView(R.id.endAutoComplete)
    AutoCompleteTextView endAutoComplete;
    @BindView(R.id.send)
    ImageButton routeButton;

    private GoogleMap map;
    private LocationHelper locationHelper;
    private DriversLoader driversLoader;
    private LatLng end, myLocation;
    private PlaceAutoCompleteAdapter adapter;
    private boolean mapReady;
    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private ArrayList<Driver> drivers;
    private ArrayList<Marker> markers;
    private Boolean fromFt = false;
    DatabaseReference reference;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        tripDirection.setOnCheckedChangeListener(this);

        locationHelper = new LocationHelper(this);
        locationHelper.setListener(this);
        locationHelper.createGoogleClient();


        driversLoader = new DriversLoader(false);
        driversLoader.setListener(this);
        driversLoader.setTripDirection(false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setTextWatchers();
        setClickListeners();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMarkerClickListener(this);
        mapReady = true;
        setUpPlaceAutoCompleteAdapter();
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = MapsActivity.this.map.getProjection().getVisibleRegion().latLngBounds;
                adapter.setBounds(BOUNDS_NIGERIA);
            }
        });

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(6.667876, 3.151196));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    private void setUpPlaceAutoCompleteAdapter() {
        googleApiClient = locationHelper.getGoogleApiClient();
        adapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, googleApiClient, BOUNDS_NIGERIA, null);
        setPlaceAdapterToView(adapter);
    }

    private void setPlaceAdapterToView(PlaceAutoCompleteAdapter adapter) {
        endAutoComplete.setAdapter(adapter);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLastLocationGotten(Location location) {
        this.myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("CanaanApp", "The latlng is " + myLocation.latitude + " " + myLocation.longitude);

        driversLoader.setLatLng(myLocation);
        driversLoader.setTripDirection(false);
        driversLoader.setListener(this);
        driversLoader.loadToFtDrivers();
        if (mapReady)
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    @Override
    public void onLocationChanged(LatLng latLng) {
        Log.d("CanaanApp", "The latlng is " + latLng.latitude + " " + latLng.longitude);
        myLocation = latLng;
        driversLoader.setLatLng(latLng);

    }

    @Override
    public void onDriversLoaded(ArrayList<Driver> drivers) {
        map.clear();
        for (Driver driver : drivers) {
            Marker marker = createMarker(driver, 0);
            markers.add(marker);
        }

    }

    private Marker createMarker(Driver driver, int position) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(driver.getLatitude(), driver.getLongitude()))
                .title(driver.getName());
        Marker marker = map.addMarker(options);
        marker.setTag(driver);

        return marker;
    }

    @Override
    public void onDriverPositionChanged(Driver driver) {

        int markerPosition = drivers.indexOf(driver);
        markers.get(markerPosition).setPosition(new LatLng(driver.getLatitude(), driver.getLongitude()));
    }


    @Override
    public void onDriverAdded(Driver driver) {
        Marker marker = createMarker(driver, 0);
        markers.add(marker);

    }

    @Override
    public void onDriverRemoved(Driver driver) {

        int index = drivers.indexOf(driver);
        drivers.remove(index);
        markers.remove(index);
    }


    private void onDestinationAutocompleteClicked(int position) {
        final PlaceAutoCompleteAdapter.PlaceAutocomplete item = adapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);
        Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(locationHelper.getGoogleApiClient(), placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                    places.release();
                    return;
                }
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                end = place.getLatLng();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        map.clear();
        switch (checkedId) {
            case R.id.toFt:
                fromFt = false;
                driversLoader = new DriversLoader(false);
                driversLoader.setLatLng(myLocation);
                driversLoader.setTripDirection(false);
                driversLoader.setListener(this);
                driversLoader.loadToFtDrivers();

                cardView.setVisibility(View.GONE);
                break;
            case R.id.fromFt:
                driversLoader.setTripDirection(true);
                fromFt = true;
                cardView.setVisibility(View.VISIBLE);
                break;

        }
    }

    @OnClick(R.id.send)
    public void findDrivers() {
        if (end == null) {
            if (getTextOfDestinationField().length() > 0) {
                setErrorOnDestinationTextField("Choose myLocation from dropdown.");
            } else {
                showToast("Please choose a destination.");
            }
        } else {
            showProgressDialog("Please wait.", "Getting drivers.");
            driversLoader = new DriversLoader(true);
            driversLoader.setLatLng(end);
            driversLoader.setListener(this);
            driversLoader.loadFromFtDrivers();
        }
    }

    private String getTextOfDestinationField() {
        return endAutoComplete.getText().toString();
    }

    private void setErrorOnDestinationTextField(String error) {
        endAutoComplete.setError(error);
    }

    private void setTextWatchers() {

        endAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                setEndToNull();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setClickListeners() {

        endAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                onDestinationAutocompleteClicked(position);

            }
        });
    }

    private void setEndToNull() {
        if (end != null) end = null;
    }

    public void showProgressDialog(String title, String message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Driver driver = (Driver) marker.getTag();
        showInfoWindow(driver);
        return false;
    }

    private void showInfoWindow(final Driver driver) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_driver_select);
        dialog.setTitle("Driver Details");

        TextView driverName = (TextView) dialog.findViewById(R.id.driverName);
        TextView driverCar = (TextView) dialog.findViewById(R.id.driverCar);
        TextView driverDistance = (TextView) dialog.findViewById(R.id.driverDistance);
        ImageButton close = (ImageButton) dialog.findViewById(R.id.close);
        Button requestDriver = (Button) dialog.findViewById(R.id.requestDriver);
        RatingBar driverRating = (RatingBar) dialog.findViewById(R.id.driverRating);

        driverName.setText(driver.getName());
        driverCar.setText(driver.getCar());
        // driverDistance.setText(driver.g());
        driverRating.setRating(driver.getRating());

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        requestDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Trip trip = createTrip(driver.getId());
                showProgressDialog("Driver request", "Waiting for response from driver");
                requestDriver(trip);
                dialog.show();
            }
        });
    }


    private void requestDriver(Trip trip) {

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("drivers").child("trip").child(trip.getDriverId()).setValue(trip);
        listenForResponseFromDriver(trip);
    }

    private void listenForResponseFromDriver(final Trip trip) {
        reference.child("drivers").child("trip").child(trip.getDriverId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dismissProgressDialog();
                if ((Boolean) dataSnapshot.child("isApproved").getValue()){
                    Intent intent = new Intent(MapsActivity.this, TripActivity.class);
                    intent.putExtra("driverid", trip.getDriverId());
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Trip createTrip(String id) {
        Trip trip = new Trip();
        trip.setDriverId(id);
        trip.setUserId(userid);
        trip.setFromFt(fromFt);
        if (fromFt){
            trip.setLatitude(end.latitude);
            trip.setLongitude(end.longitude);
        }else {
            trip.setLatitude(myLocation.latitude);
            trip.setLongitude(myLocation.longitude);
        }
        trip.setApproved(false);
        trip.setCompleted(false);
        return trip;
    }
}

