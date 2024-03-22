import { makeEmbyUrl } from "./config";
import { PlaybackInfo } from "../model/PlaybackInfo";
import device from "./device.json"
import { EmbySite } from "@model/EmbySite";
import { MediaDetail } from "@model/MediaDetail";

export async function getPlaybackInfo(site: EmbySite, id: number) {
    const params = {
        StartTimeTicks: 0,
        IsPlayback: false,
        AutoOpenLiveStream: false,
        MaxStreamingBitrate: 1500000,
        UserId: site.user.User.Id,
        "X-Emby-Client": "Emby Web",
        "X-Emby-Device-Name": "Microsoft Edge macOS",
        "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
        "X-Emby-Client-Version": "4.7.13.0",
        "X-Emby-Token": site.user.AccessToken,
        "X-Emby-Language": "zh-cn",
        reqformat: "json"
    }
    const url = makeEmbyUrl(params, `emby/Items/${id}/PlaybackInfo`, site.server)
    const response = await fetch(url, {method: "POST", body: JSON.stringify(device), headers: {
        "content-type": "text/plain"
    }})
    return await response.json() as PlaybackInfo
}

export const getPlayUrl = (detail?: MediaDetail) => {
    const sources = detail?.MediaSources ?? []
    const urls = sources.map((source) => {
        console.log(source)
        if (source.Container === "strm") {
            return source.Path
        } else {
            return source.DirectStreamUrl
        }
    })
    return urls?.[0]
}