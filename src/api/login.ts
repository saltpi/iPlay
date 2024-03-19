import { User } from "../model/User";
import { EmbyConfig, makeEmbyUrl } from "./config";

export async function login(username: string, password: string, endpoint: EmbyConfig) {
  const params = {
    "X-Emby-Client": "Emby Web",
    "X-Emby-Device-Name": "Microsoft Edge macOS",
    "X-Emby-Device-Id": "feed8217-7abd-4d2d-a561-ed21c0b9c30e",
    "X-Emby-Client-Version": "4.7.13.0",
    "X-Emby-Language": "zh-cn"
  }
  const url = makeEmbyUrl(params, `emby/Users/authenticatebyname`, endpoint)
  try {
    const response = await fetch(url, {
      "headers": {
        "accept": "application/json",
        "accept-language": "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
        "content-type": "application/x-www-form-urlencoded; charset=UTF-8"
      },
      "referrerPolicy": "strict-origin-when-cross-origin",
      "body": `Username=${username}&Pw=${password}`,
      "method": "POST"
    })
    const data = await response.json() as User
    return data
  } catch (e) {
    console.error(e)
    return null
  }
}