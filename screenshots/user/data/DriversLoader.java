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

/**
 * Created by Digz on 28/03/2017.
 */

public class DriversLoader implements ValueEventListener, ChildEventListener {

    private DatabaseReference databaseReference;
    private ArrayList<Driver> drivers;
    private LatLng latLng;
    private DriversLoaderListener listener;

    public DriversLoader() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers");
    }


    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setListener(DriversLoaderListener listener){
        this.listener = listener;
    }

    public void loadDrivers() {
        drivers = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Driver driver = new Driver();
            driver.setId(snapshot.getKey());
            driver.setName((String) snapshot.child("name").getValue());
            driver.setCar((String) snapshot.child("car").getValue());
            driver.setRating((Double) snapshot.child("rating").getValue());
            driver.setLatitude((Double) snapshot.child("latitude").getValue());
            driver.setLongitude((Double) snapshot.child("longitude").getValue());
            determineDrivers(driver);
        }
        listener.onDriversLoaded(drivers);
        databaseReference.addChildEventListener(this);
    }

    private void determineDrivers(Driver driver) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        if (driver.getLatitude() > latitude - 0.05 && driver.getLatitude() < latitude + 0.05) {
            if (driver.getLongitude() > longitude - 0.05 && driver.getLongitude() < longitude + 0.05) {
                drivers.add(driver);
                return;
            }
        }
        if (drivers.contains(driver)) drivers.remove(driver);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String s) {
        Driver driver = new Driver();
        driver.setId(snapshot.getKey());
        driver.setName((String) snapshot.child("name").getValue());
        driver.setCar((String) snapshot.child("car").getValue());
        driver.setRating((Double) snapshot.child("rating").getValue());
        driver.setLatitude((Double) snapshot.child("latitude").getValue());
        driver.setLongitude((Double) snapshot.child("longitude").getValue());
        determineDrivers(driver);
        listener.onDriversLoaded(drivers);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public interface DriversLoaderListener{
        void onDriversLoaded(ArrayList<Driver> drivers);
    }
}
