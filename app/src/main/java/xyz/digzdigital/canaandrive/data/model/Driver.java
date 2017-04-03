package xyz.digzdigital.canaandrive.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Digz on 29/03/2017.
 */

public class Driver implements Parcelable{
    private String id;
    private String name;
    private float rating;
    private String car;
    private double latitude;
    private double longitude;
    private double destLatitude;
    private double destLongitude;
    private boolean isOnline;
    private boolean isAvailable;
    private boolean fromFt;
    private String token;


    public Driver() {
    }

    public Driver(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.rating = in.readFloat();
        this.car = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.destLatitude = in.readDouble();
        this.destLongitude = in.readDouble();
        this.isOnline = in.readInt() == 1;
        this.isAvailable = in.readInt() == 1;
        this.fromFt = in.readInt() == 1;
        this.token = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeFloat(rating);
        parcel.writeString(car);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeDouble(destLatitude);
        parcel.writeDouble(destLongitude);
        int state = 0;
        if (isOnline)state = 1;
        parcel.writeInt(state);
        state = 0;
        if (isAvailable)state = 1;
        parcel.writeInt(state);
        state = 0;
        if (isFromFt())state = 1;
        parcel.writeInt(state);
        parcel.writeString(token);
    }

    public static final Parcelable.Creator<Driver> CREATOR = new Parcelable.Creator<Driver>(){
        @Override
        public Driver createFromParcel(Parcel parcel){
            return new Driver(parcel);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

    @Override
    public int describeContents(){
        return hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = (float) rating;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDestLatitude() {
        return destLatitude;
    }

    public void setDestLatitude(double destLatitude) {
        this.destLatitude = destLatitude;
    }

    public double getDestLongitude() {
        return destLongitude;
    }

    public void setDestLongitude(double destLongitude) {
        this.destLongitude = destLongitude;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isFromFt() {
        return fromFt;
    }

    public void setFromFt(boolean fromFt) {
        this.fromFt = fromFt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
