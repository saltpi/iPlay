import { View } from "@model/View";
import { makeEmbyUrl } from "./config";
import { Media } from "@model/Media";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { EmbyResponse } from "@model/EmbyResponse";
import { Episode } from "@model/Episode";
import { EmbySite } from "@model/EmbySite";
import { Version } from "@helper/device";
import { UserData } from "@model/UserData";
import { PlaybackData, kPlaybackData } from "@model/PlaybackData";
import { logger } from "@helper/log";

export const EMBY_CLIENT_HEADERS = {
    "X-Emby-Client": Version.displayName,
    "X-Emby-Device-Name": "",
    "X-Emby-Device-Id": "",
    "X-Emby-Client-Version": Version.versionCode, 
} as any

export async function getView(site: EmbySite) {
    const token = site.user.AccessToken
    const uid = site.user.User.Id
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Views`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": token,
        }
    });
    const data = await response.json() as View
    return data
}

export async function getLatestMedia(site: EmbySite, parentId: number) {
    const params = {
        Limit: 16,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate,Overview",
        ImageTypeLimit: 1,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ParentId: parentId,
        "X-Emby-Language": "zh-cn"
    }
    const uid = site.user.User.Id
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Items/Latest`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as Media[]
    return data
}

export async function getMedia(site: EmbySite, id: number) {
    const params = {
        ...EMBY_CLIENT_HEADERS,
        "X-Emby-Token": site.user.AccessToken,
        "X-Emby-Language": "zh-cn"
    }
    const uid = site.user.User.Id
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Items/${id}`, site.server)
    const response = await fetch(url)
    const data = await response.json() as MediaDetail
    return data
}

export const getActor = getMedia

export async function getResume(site: EmbySite, type: "Video"|"Audio" = "Video") {
    const params = {
        Recursive: true,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate,Overview",
        ImageTypeLimit: 1,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        MediaTypes: type,
        Limit: 12,
        "X-Emby-Language": "zh-cn"
    }
    const uid = site.user.User.Id
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Items/Resume`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data.Items
}

export async function getRecommendations(site: EmbySite) {
    const uid = site.user.User.Id
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Suggestions`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data.Items
}

export async function getSeasons(site: EmbySite, id: number) {
    const params = {
        UserId: site.user.User.Id,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio",
        EnableTotalRecordCount: false,
        "X-Emby-Language": "zh-cn"
    }
    const url = makeEmbyUrl(params, `emby/Shows/${id}/Seasons`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Season>
    return data.Items
}

export async function getEpisodes(site: EmbySite, vid: number, sid: number) {
    const params = {
        UserId: site.user.User.Id,
        SeasonId: sid,
        Fields: "Overview,PrimaryImageAspectRatio",
        EnableTotalRecordCount: false,
        "X-Emby-Language": "zh-cn"
    }
    const url = makeEmbyUrl(params, `emby/Shows/${vid}/Episodes`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Episode>
    return data.Items
}

interface ItemOptions {
    IncludeItemTypes?: string
    PersonIds?: string
    Filters?: string
    page?: number
    type?: "Series"|"Episode"|"Movie"
}

export async function getItem(site: EmbySite, options: ItemOptions) {
    const { type = "Series", page = 0, ...rest } = options
    const uid = site.user.User.Id
    const params = {
        UserId: site.user.User.Id,
        SortBy: "SortName",
        SortOrder: "Ascending",
        IncludeItemTypes: type,
        Recursive: true,
        Fields: "BasicSyncInfo,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix",
        StartIndex: page * 50,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ImageTypeLimit: 1,
        Limit: 50,
        "X-Emby-Language": "zh-cn",
        ...rest
    }
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Items`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export const kEmbyItemPageSize = 50
export type CollectionOptions = {
    StartIndex?: number
    Limit?: number
    SortBy?: string
}
export async function getCollection(site: EmbySite, cid: number, type: "Series"|"Movie" = "Series", {
    StartIndex = 0,
    Limit = kEmbyItemPageSize,
    SortBy = "DateCreated,SortName"
}: CollectionOptions) {
    const uid = site.user.User.Id
    const params = {
        UserId: site.user.User.Id,
        SortBy,
        SortOrder: "Ascending",
        IncludeItemTypes: type,
        Recursive: true,
        Fields: "BasicSyncInfo,SortName,Overview,CanDelete,Container,PrimaryImageAspectRatio,Prefix,DateCreated",
        ParentId: cid,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ImageTypeLimit: 1,
        "X-Emby-Language": "zh-cn",
        StartIndex,
        Limit
    }
    const url = makeEmbyUrl(params, `emby/Users/${uid}/Items`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export async function searchRecommend(site: EmbySite) {
    const params = {
        UserId: site.user.User.Id,
        SortBy: "IsFavoriteOrLiked,Random",
        IncludeItemTypes: "Movie,Series,MusicArtist",
        Limit: 20,
        Recursive: true,
        ImageTypeLimit: 0,
        EnableImages: false,
        EnableTotalRecordCount: false
    }
    const url = makeEmbyUrl(params, `/emby/Items`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export async function lookupItem(site: EmbySite, title: string) {
    const params = {
        UserId: site.user.User.Id,
        SortBy: "SortName",
        SortOrder: "Ascending",
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate",
        StartIndex: 0,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ImageTypeLimit: 1,
        Recursive: true,
        SearchTerm: title,
        GroupProgramsBySeries: true,
        Limit: 50
    }
    const url = makeEmbyUrl(params, `/emby/Items`, site.server)
    const response = await fetch(url, {
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export async function markFavorite(site: EmbySite, id: number, favorite: boolean) {
    const uid = site.user.User.Id
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const url = favorite ?
        makeEmbyUrl(params, `emby/Users/${uid}/FavoriteItems/${id}`, site.server) :
        makeEmbyUrl(params, `emby/Users/${uid}/FavoriteItems/${id}/Delete`, site.server)
    const response = await fetch(url, {
        method: "POST",
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "X-Emby-Token": site.user.AccessToken,
        }
    })
    const data = await response.json() as UserData
    return data
}

export async function play(site: EmbySite, 
    path: ""|"Progress"|"Stopped", data: Partial<PlaybackData>) {
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const body: PlaybackData = {
        ...kPlaybackData,
        ...data
    }
    const url = makeEmbyUrl(params, `emby/Sessions/Playing/${path}`, site.server)
    const response = await fetch(url, {
        method: "POST",
        headers: {
            ...EMBY_CLIENT_HEADERS,
            "Content-Type": "application/json",
            "X-Emby-Token": site.user.AccessToken,
            reqformat: "json"
        },
        body: JSON.stringify(body)
    })
    const json = await response.json() as UserData
    return json
}

export async function startPlay(site: EmbySite, info: Partial<PlaybackData>) {
    return await play(site, "", info)
}

export async function trackPlay(site: EmbySite, info: Partial<PlaybackData>) {
    return await play(site, "Progress", info)
}

export async function stopPlay(site: EmbySite, info: Partial<PlaybackData>) {
    return await play(site, "Stopped", info)
}