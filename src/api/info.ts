import { Info } from "../model/Info"
import { makeUrl } from "./config"

export async function getPublicInfo() {
    const url = makeUrl(null, `emby/system/info/public`)
    const response = await fetch(url)
    const info = await response.json() as Info
    return info
}