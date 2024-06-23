package model;

public class Application {
    private long databaseId;
    private ApplicationPath applicationPath;
    private String name;

    public Application(String name, ApplicationPath applicationPath, long databaseId) {
        this.databaseId = databaseId;
        this.applicationPath = applicationPath;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ApplicationPath getApplicationPath() {
        return applicationPath;
    }

    public long getDatabaseId() {
        return databaseId;
    }
}
