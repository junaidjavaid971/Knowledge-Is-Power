package app.com.knowledge.power.models;

public class UserLocation {
    public double latitude, longitude;
    public int colorCode;

    public UserLocation(double latitude, double longitude, int colorCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.colorCode = colorCode;
    }

    public UserLocation() {
    }
}
