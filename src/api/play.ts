import { makeEmbyUrl } from "./config";
import { PlaybackInfo } from "../model/PlaybackInfo";
import device from "./device.json"
import { EmbySite } from "@model/EmbySite";
import { MediaDetail } from "@model/MediaDetail";
import { CLIENT_HEADERS } from "./view";

export async function getPlaybackInfo(site: EmbySite, id: number) {
    const params = {
        StartTimeTicks: 0,
        IsPlayback: false,
        AutoOpenLiveStream: false,
        MaxStreamingBitrate: 1500000,
        UserId: site.user.User.Id,
        "X-Emby-Token": site.user.AccessToken,
        "X-Emby-Language": "zh-cn",
        reqformat: "json"
    }
    const url = makeEmbyUrl(params, `emby/Items/${id}/PlaybackInfo`, site.server)
    const headers = {
        ...CLIENT_HEADERS,
        "X-Emby-Token": site.user.AccessToken,
        "content-type": "text/plain"
    }
    const response = await fetch(url, {method: "POST", body: "", headers})
    return await response.json() as PlaybackInfo
}

export const getPlayUrl = (detail?: MediaDetail) => {
    const sources = detail?.MediaSources ?? []
    const urls = sources.map((source) => {
        if (source.Container === "strm") {
            return source.Path
        } else {
            return source.DirectStreamUrl
        }
    })
    return urls?.[0] ?? null
}