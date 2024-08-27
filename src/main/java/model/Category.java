package model;

public class Category {
    private final String name;
    private final String url;
    private final long databaseId;
    private final long parentId;

    public Category(String name, String url, long databaseId, long parentId) {
        this.name = name;
        this.url = url;
        this.databaseId = databaseId;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
