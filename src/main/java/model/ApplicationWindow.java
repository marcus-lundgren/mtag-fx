package model;

public class ApplicationWindow {
    private Long databaseId = null;
    private String title;
    private Application application;

    public ApplicationWindow(String title, Application application) {
        this.title = title;
        this.application = application;
    }

    public ApplicationWindow(String title, Application application, Long databaseId) {
        this(title, application);
        this.databaseId = databaseId;
    }

    public String getTitle() {
        return title;
    }

    public Application getApplication() {
        return application;
    }
}
