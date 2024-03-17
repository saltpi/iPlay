import { EmbySite } from "@model/EmbySite";
import { ENV, EmbyConfig } from "../helper/env";
import { Map } from "../model/Map";

export type { EmbyConfig } from "../helper/env"

export const DEFAULT_EMBY_CONFIG: EmbyConfig = ENV.emby

export const config = {
    emby: DEFAULT_EMBY_CONFIG,
    tmdb: {
        api_key: ENV.tmdb.api_key
    }
}

export function makeEmbyUrl(params: Map<string, any>|null, path: string, emby: EmbyConfig) {
    console.log(`makeEmbyUrl`, emby)
    const url = new URL(`${emby.protocol ?? "https"}://${emby.host}:${emby.port ?? 443}${emby.path}${path}`)
    params && Object.entries(params).forEach(([key, value]) => {
        if (typeof value === "string") {
            url.searchParams.append(key, value)
        } else {
            url.searchParams.append(key, String(value))
        }
    })
    return url
}

export interface ImageProps {
    maxHeight: number
    maxWidth: number
    tag: string
    quality: number
}

export function imageUrl(site: EmbySite, id: string|number, options: string|Partial<ImageProps>|null, type: "Primary"|string = "Primary") {
    if (typeof options === "string") {
        return `${config.emby.protocol}://${config.emby.host}:${config.emby.port}${config.emby.path}emby/Items/${id}/Images/${type}?maxHeight=338&maxWidth=600&tag=${options}&quality=90`
    } else {
        const url = new URL(`${config.emby.protocol}://${config.emby.host}:${config.emby.port}${config.emby.path}emby/Items/${id}/Images/${type}`)
        options && Object.entries(options).forEach(([key, value]) => {
            url.searchParams.set(key, String(value))
        })
        return url.href
    }
}

export function avatorUrl(id: string, options: string|Partial<ImageProps>, type: "Primary" = "Primary") {
    return `${config.emby.protocol}://${config.emby.host}:${config.emby.port}${config.emby.path}emby/Users/${id}/Images/${type}?height=152&tag=${options}&quality=90`
}

export function playUrl(path: string) {
    if (path?.startsWith("http")) return path
    return `${config.emby.protocol}://${config.emby.host}:${config.emby.port}${config.emby.path}emby${path}`
}