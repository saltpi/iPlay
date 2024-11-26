package top.ourfor.app.iplayx.common.type;

public enum ServerType {
    None(""),
    Emby("emby"),
    Jellyfin("jellyfin"),
    Plex("plex"),
    OneDrive("onedrive"),
    WebDAV("webdav"),
    Alist("alist"),
    IPTV("iptv"),
    Cloud189("189");

    private String value;

    ServerType(String value) {
        this.value = value;
    }
}
