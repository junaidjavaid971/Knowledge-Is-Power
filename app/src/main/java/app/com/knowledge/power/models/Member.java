package app.com.knowledge.power.models;

public class Member {
    public User user;
    public String joinedSince;

    public Member(User user, String joinedSince) {
        this.user = user;
        this.joinedSince = joinedSince;
    }

    public Member() {
    }
}
