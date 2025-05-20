package top.ourfor.app.iplay.common.type;

public enum ServerType {
    None(""),
    Emby("emby"),
    Jellyfin("jellyfin"),
    Plex("plex"),
    OneDrive("onedrive"),
    WebDAV("webdav"),
    Alist("alist"),
    IPTV("iptv"),
    iPlay("iplay"),
    Cloud189("189"),
    Local("local");

    private String value;

    ServerType(String value) {
        this.value = value;
    }
}
