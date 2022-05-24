package app.com.knowledge.power.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;

import app.com.knowledge.power.MessageTypes;

public class Message implements Serializable {
    public User sender;
    public String message;
    public String datetime;
    public String messageId;
    public String groupId;
    public String locationId;
    public MessageTypes type;
    public String imgPath;
    public UserLocation latLng;

    public Message() {

    }

    public Message(User sender, String messageId, String message, String datetime, String groupId,
                   MessageTypes type, String imgPath, String locationId, UserLocation latLng) {
        this.sender = sender;
        this.message = message;
        this.datetime = datetime;
        this.type = type;
        this.imgPath = imgPath;
        this.messageId = messageId;
        this.groupId = groupId;
        this.locationId = locationId;
        this.latLng = latLng;
    }
}