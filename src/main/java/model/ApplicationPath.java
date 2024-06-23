package model;

public class ApplicationPath {
    private long databaseId;
    private String path;

    public ApplicationPath(String path, long databaseId) {
        this.databaseId = databaseId;
        this.path = path;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public String getPath() {
        return path;
    }
}
