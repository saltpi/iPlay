import { EmbySite } from "@model/EmbySite"
import { Info } from "@model/Info"
import { makeEmbyUrl } from "./config"

export async function getPublicInfo(site: EmbySite) {
    const url = makeEmbyUrl(null, `emby/system/info/public`, site.server)
    const response = await fetch(url)
    const info = await response.json() as Info
    return info
}