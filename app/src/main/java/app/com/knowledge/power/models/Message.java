package app.com.knowledge.power.models;

import java.io.Serializable;

import app.com.knowledge.power.MessageTypes;

public class Message implements Serializable {
    public String sender;
    public String receiver;
    public String message;
    public String datetime;
    public String id;
    public MessageTypes type;
    public String imgPath;

    public Message() {

    }

    public Message(String sender, String receiver, String message, String datetime, String id, MessageTypes type, String imgPath) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.datetime = datetime;
        this.id = id;
        this.type = type;
        this.imgPath = imgPath;
    }
}