import { User } from "../model/User";
import { View } from "@model/View";
import { DEFAULT_EMBY_CONFIG, makeUrl } from "./config";
import { Media } from "@model/Media";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { EmbyResponse } from "@model/EmbyResponse";
import { Episode } from "@model/Episode";

export async function getView(user: User) {
    const token = user.AccessToken
    const uid = user.User.Id
    const did = "feed8217-7abd-4d2d-a561-ed21c0b9c30e"
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const url = makeUrl(params, `emby/Users/${uid}/Views`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Device-Id": did,
            "X-Emby-Token": token,
        }
    });
    const data = await response.json() as View
    return data
}

export async function getLatestMedia(user: User, parentId: number) {
    const params = {
        Limit: 16,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear,Status,EndDate,Overview",
        ImageTypeLimit: 1,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ParentId: parentId,
        "X-Emby-Language": "zh-cn"
    }
    const uid = user.User.Id
    const url = makeUrl(params, `emby/Users/${uid}/Items/Latest`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Token": user.AccessToken,
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
        }
    })
    const data = await response.json() as Media[]
    return data
}

export async function getMedia(user: User, id: number) {
    const params = {
        "X-Emby-Client": "Emby Web",
        "X-Emby-Device-Name": "Microsoft Edge macOS",
        "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
        "X-Emby-Client-Version": "4.7.13.0",
        "X-Emby-Token": user.AccessToken,
        "X-Emby-Language": "zh-cn"
    }
    const uid = user.User.Id
    const url = makeUrl(params, `emby/Users/${uid}/Items/${id}`)
    const response = await fetch(url)
    const data = await response.json() as MediaDetail
    return data
}

export async function getResume(user: User) {
    const params = {
        Recursive: true,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,ProductionYear",
        ImageTypeLimit: 1,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        MediaTypes: "Video",
        Limit: 12,
        "X-Emby-Language": "zh-cn"
    }
    const uid = user.User.Id
    const url = makeUrl(params, `emby/Users/${uid}/Items/Resume`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data.Items
}

export async function getRecommendations(user: User) {
    const uid = user.User.Id
    const params = {
        "X-Emby-Language": "zh-cn"
    }
    const url = makeUrl(params, `emby/Users/${uid}/Suggestions`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data.Items
}

export async function getSeasons(user :User, id: number) {
    const params = {
        UserId: user.User.Id,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio",
        EnableTotalRecordCount: false,
        "X-Emby-Language": "zh-cn"
    }
    const url = makeUrl(params, `emby/Shows/${id}/Seasons`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Season>
    return data.Items
}

export async function getEpisodes(user: User, vid: number, sid: number) {
    const params = {
        UserId: user.User.Id,
        SeasonId: sid,
        Fields: "Overview,PrimaryImageAspectRatio",
        EnableTotalRecordCount: false,
        "X-Emby-Language": "zh-cn"
    }
    const url = makeUrl(params, `emby/Shows/${vid}/Episodes`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Episode>
    return data.Items
}



export async function getCollection(user: User, cid: number, type: "Series"|"Movie" = "Series", page: number = 0) {
    const uid = user.User.Id
    const params = {
        UserId: user.User.Id,
        SortBy: "SortName",
        SortOrder: "Ascending",
        IncludeItemTypes: type,
        Recursive: true,
        Fields: "BasicSyncInfo,CanDelete,Container,PrimaryImageAspectRatio,Prefix",
        StartIndex: page * 50,
        ParentId: cid,
        EnableImageTypes: "Primary,Backdrop,Thumb",
        ImageTypeLimit: 1,
        Limit: 50,
        "X-Emby-Language": "zh-cn"
    }
    const url = makeUrl(params, `emby/Users/${uid}/Items`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export async function searchRecommend(user: User) {
    const params = {
        UserId: user.User.Id,
        SortBy: "IsFavoriteOrLiked,Random",
        IncludeItemTypes: "Movie,Series,MusicArtist",
        Limit: 20,
        Recursive: true,
        ImageTypeLimit: 0,
        EnableImages: false,
        EnableTotalRecordCount: false
    }
    const url = makeUrl(params, `/emby/Items`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}

export async function lookupItem(user: User, title: string) {
    const params = {
        UserId: user.User.Id,
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
    const url = makeUrl(params, `/emby/Items`)
    const response = await fetch(url, {
        headers: {
            "X-Emby-Client": "Emby Web",
            "X-Emby-Device-Name": "Microsoft Edge macOS",
            "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
            "X-Emby-Client-Version": "4.7.13.0",
            "X-Emby-Token": user.AccessToken,
        }
    })
    const data = await response.json() as EmbyResponse<Media>
    return data
}