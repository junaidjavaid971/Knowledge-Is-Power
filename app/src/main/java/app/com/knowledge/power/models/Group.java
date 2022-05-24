package app.com.knowledge.power.models;

import java.util.ArrayList;

public class Group {
    public String id, adminId, date, groupCode, groupName;

    public Group() {
    }

    public Group(String id, String adminId, String date, String groupCode, String groupName) {
        this.id = id;
        this.adminId = adminId;
        this.date = date;
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

}
