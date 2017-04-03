package xyz.digzdigital.canaandrive.ui.driver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.adapter.PlaceAutoCompleteAdapter;
import xyz.digzdigital.canaandrive.location.LocationHelper;

public class DriverActivity extends AppCompatActivity implements LocationHelper.LocationHelperListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final LatLngBounds BOUNDS_NIGERIA = new LatLngBounds(new LatLng(5.065341647205726, 2.9987719580531),
            new LatLng(9.9, 5.9));
    private static final String LOG_TAG = "DIGZ";

    @BindView(R.id.tripDirection)
    RadioGroup tripDirection;
    @BindView(R.id.rideOn)
    ToggleButton rideOn;
    @BindView(R.id.autoCompleteCardview)
    CardView cardView;
    @BindView(R.id.endAutoComplete)
    AutoCompleteTextView endAutoComplete;

    boolean isAvailable = false;
    private DatabaseReference reference, listenerRef;
    private LocationHelper helper;
    private String id;
    private LatLng end;
    private GoogleApiClient googleApiClient;
    private ProgressDialog progressDialog;
    private PlaceAutoCompleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        ButterKnife.bind(this);
        helper = new LocationHelper(this);
        helper.setListener(this);
        helper.createGoogleClient();
        helper.onStart();

        tripDirection.setOnCheckedChangeListener(this);
        rideOn.setOnClickListener(this);
        id = getIntent().getStringExtra("driverid");
        reference = FirebaseDatabase.getInstance().getReference().child("drivers").child(id);
        listenerRef = FirebaseDatabase.getInstance().getReference().child("trips").child(id);
        listenerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean newTrip = (Boolean) dataSnapshot.child("newTrip").getValue();
                if (newTrip) {
                    onRequestReceived();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        reference.child("isOnline").onDisconnect().setValue(false);

        reference.child("isOnline").setValue(true);



        setToken();
        setTextWatchers();
        setClickListeners();


    }

    private void listenersForRequests() {

    }

    private void setToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference().child("notification").child(id).setValue(token);
    }

    @Override
    public void onConnected(Bundle bundle) {
        setUpPlaceAutoCompleteAdapter();
        adapter.setBounds(BOUNDS_NIGERIA);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLastLocationGotten(Location location) {
        if (!isAvailable) return;
        reference.child("latitude").setValue(location.getLatitude());
        reference.child("longitude").setValue(location.getLongitude());
    }

    @Override
    public void onLocationChanged(LatLng latLng) {
        if (!isAvailable) return;
        reference.child("latitude").setValue(latLng.latitude);
        reference.child("longitude").setValue(latLng.longitude);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.onResume();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.toFt:
                reference.child("fromFt").setValue(false);
                break;
            case R.id.fromFt:
                reference.child("fromFt").setValue(true);
                reference.child("isAvailable").setValue(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {

        if (rideOn.isChecked()) {
            isAvailable = true;
            reference.child("isAvailable").setValue(true);
        }
        if (!rideOn.isChecked()) {
            isAvailable = false;
            reference.child("isAvailable").setValue(false);
        }
    }

    private void setUpPlaceAutoCompleteAdapter() {
        googleApiClient = helper.getGoogleApiClient();
        adapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, googleApiClient, BOUNDS_NIGERIA, null);
        setPlaceAdapterToView(adapter);
    }

    private void setPlaceAdapterToView(PlaceAutoCompleteAdapter adapter) {
        endAutoComplete.setAdapter(adapter);

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
                .getPlaceById(helper.getGoogleApiClient(), placeId);
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

    @OnClick(R.id.send)
    public void setDestination() {
        if (end == null) {
            if (getTextOfDestinationField().length() > 0) {
                setErrorOnDestinationTextField("Choose myLocation from dropdown.");
            } else {
                Toast.makeText(this, "Please choose a destination.", Toast.LENGTH_SHORT).show();
            }
        } else {
            reference.child("destLatitude").setValue(end.latitude);
            reference.child("destLongitude").setValue(end.longitude);
            reference.child("isAvailable").setValue(true);

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

    private void onRequestReceived() {
        reference.child("isAvailable").setValue(false);
        showAlertDialog();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("New Ride Request")
                .setTitle("Would you like to accept this new request");

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listenerRef.child("approved").setValue(true);
            }
        });

        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listenerRef.child("approved").setValue(false);
            }
        });

        builder.create();

    }

}
