package app.com.knowledge.power.models;

public class User {

    public String id, name, email, contactNumber, profilePicUrl;

    public User() {

    }

    public User(String id, String name, String email, String contactNumber, String profilePicUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.profilePicUrl = profilePicUrl;
    }
}
