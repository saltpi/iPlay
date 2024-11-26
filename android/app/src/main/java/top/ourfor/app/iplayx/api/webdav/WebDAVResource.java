package top.ourfor.app.iplayx.api.webdav;

public class WebDAVResource {
    private String href;
    private String displayName;
    private String lastModified;
    private boolean isCollection;

    // Constructors, getters, and setters
    public WebDAVResource(String href, String displayName, String lastModified, boolean isCollection) {
        this.href = href;
        this.displayName = displayName;
        this.lastModified = lastModified;
        this.isCollection = isCollection;
    }

    public String getHref() {
        return href;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public boolean isCollection() {
        return isCollection;
    }

    @Override
    public String toString() {
        return "WebDAVResource{" +
                "href='" + href + '\'' +
                ", displayName='" + displayName + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", isCollection=" + isCollection +
                '}';
    }
}