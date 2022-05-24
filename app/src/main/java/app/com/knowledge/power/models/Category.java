package app.com.knowledge.power.models;

public class Category {
    public int color;
    public String categoryName, id, groupId;

    public Category(int color, String categoryName, String id, String groupId) {
        this.color = color;
        this.categoryName = categoryName;
        this.id = id;
        this.groupId = groupId;
    }

    public Category() {
    }
}
