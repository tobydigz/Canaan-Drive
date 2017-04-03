package xyz.digzdigital.canaandrive.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import xyz.digzdigital.canaandrive.data.model.Driver;

public class DriversLoader implements ValueEventListener, ChildEventListener {

    private DatabaseReference databaseReference;
    private ArrayList<Driver> drivers;
    private LatLng latLng;
    private DriversLoaderListener listener;
    private Boolean fromFt;

    public DriversLoader(Boolean fromFt) {
        this.fromFt = fromFt;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers");
    }

    public void setTripDirection(Boolean fromFt) {
        this.fromFt = fromFt;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setListener(DriversLoaderListener listener) {
        this.listener = listener;
    }

    public void loadToFtDrivers() {
        drivers = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(this);
    }

    public void loadFromFtDrivers() {
        drivers = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            if ((Boolean) snapshot.child("isOnline").getValue() && (Boolean) snapshot.child("isAvailable").getValue()) {
                Driver driver = new Driver();
                driver.setId(snapshot.getKey());
                driver.setName((String) snapshot.child("name").getValue());
                driver.setCar((String) snapshot.child("car").getValue());
                try {
                    driver.setRating((Double) snapshot.child("rating").getValue());
                }catch (ClassCastException e){
                    driver.setRating((Long) snapshot.child("rating").getValue());
                }
                driver.setLatitude((Double) snapshot.child("latitude").getValue());
                driver.setLongitude((Double) snapshot.child("longitude").getValue());
                if (validateDriver(driver)) drivers.add(driver);
            }
        }
        listener.onDriversLoaded(drivers);
        if (!fromFt) databaseReference.addChildEventListener(this);
    }

    private boolean validateDriver(Driver driver) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        if (driver.isFromFt() != fromFt) {
            return false;
        }

        if (fromFt) {
            if (driver.getDestLatitude() > latitude - 0.05 && driver.getDestLatitude() < latitude + 0.05) {
                if (driver.getDestLongitude() > longitude - 0.05 && driver.getDestLongitude() < longitude + 0.05) {
                    return true;
                }
            }
            return false;
        }

        if (driver.getLatitude() > latitude - 0.05 && driver.getLatitude() < latitude + 0.05) {
            if (driver.getLongitude() > longitude - 0.05 && driver.getLongitude() < longitude + 0.05) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onChildAdded(DataSnapshot snapshot, String s) {
        /*if (!(Boolean) snapshot.child("isOnline").getValue() || !(Boolean) snapshot.child("isAvailable").getValue())
            return;
        Driver driver = new Driver();
        driver.setId(snapshot.getKey());
        driver.setName((String) snapshot.child("name").getValue());
        driver.setCar((String) snapshot.child("car").getValue());
        try {
            driver.setRating((Double) snapshot.child("rating").getValue());
        }catch (ClassCastException e){
            driver.setRating((Long) snapshot.child("rating").getValue());
        }
        driver.setLatitude((Double) snapshot.child("latitude").getValue());
        driver.setLongitude((Double) snapshot.child("longitude").getValue());
        if (validateDriver(driver))
            listener.onDriverAdded(driver);*/
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {
        if ((Boolean) snapshot.child("isOnline").getValue() && (Boolean) snapshot.child("isAvailable").getValue()){

        Driver driver = new Driver();
        driver.setId(snapshot.getKey());
        driver.setName((String) snapshot.child("name").getValue());
        driver.setCar((String) snapshot.child("car").getValue());
        try {
            driver.setRating((Double) snapshot.child("rating").getValue());
        }catch (ClassCastException e){
            driver.setRating((Long) snapshot.child("rating").getValue());
        }
        driver.setLatitude((Double) snapshot.child("latitude").getValue());
        driver.setLongitude((Double) snapshot.child("longitude").getValue());
        if (validateDriver(driver))
            listener.onDriverPositionChanged(driver);}
        // else listener.onDriverRemoved(driver);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        Driver driver = new Driver();
        driver.setId(snapshot.getKey());
        driver.setName((String) snapshot.child("name").getValue());
        driver.setCar((String) snapshot.child("car").getValue());
        try {
            driver.setRating((Double) snapshot.child("rating").getValue());
        }catch (ClassCastException e){
            driver.setRating((Long) snapshot.child("rating").getValue());
        }
        driver.setLatitude((Double) snapshot.child("latitude").getValue());
        driver.setLongitude((Double) snapshot.child("longitude").getValue());
        listener.onDriverRemoved(driver);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface DriversLoaderListener {
        void onDriversLoaded(ArrayList<Driver> drivers);

        void onDriverPositionChanged(Driver driver);

        void onDriverAdded(Driver driver);

        void onDriverRemoved(Driver driver);
    }
}
