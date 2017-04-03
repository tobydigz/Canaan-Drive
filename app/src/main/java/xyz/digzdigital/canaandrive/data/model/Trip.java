package xyz.digzdigital.canaandrive.data.model;



public class Trip {
    private String userId;
    private String driverId;
    private String driverName;
    private String userName;
    private boolean fromFt;
    private double latitude;
    private double longitude;
    private boolean isApproved;
    private boolean isCompleted;
    private boolean newTrip;

    public Trip() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFromFt() {
        return fromFt;
    }

    public void setFromFt(boolean fromFt) {
        this.fromFt = fromFt;
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isNewTrip() {
        return newTrip;
    }

    public void setNewTrip(boolean newTrip) {
        this.newTrip = newTrip;
    }
}
