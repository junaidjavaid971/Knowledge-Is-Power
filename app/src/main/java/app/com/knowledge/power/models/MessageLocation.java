package app.com.knowledge.power.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MessageLocation implements Serializable {
    public LatLng latLng;
    public String locationId;
    public int colorCode;

    public MessageLocation() {
    }

    public MessageLocation(LatLng latLng, String locationId, int colorCode) {
        this.latLng = latLng;
        this.locationId = locationId;
        this.colorCode = colorCode;
    }
}
