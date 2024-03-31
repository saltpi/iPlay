import { User } from "../model/User";
import { EmbyConfig, makeEmbyUrl } from "./config";
import { EMBY_CLIENT_HEADERS } from "./view";

export async function login(username: string, password: string, endpoint: EmbyConfig) {
  const params = {
    ...EMBY_CLIENT_HEADERS,
    "X-Emby-Language": "zh-cn"
  }
  const url = makeEmbyUrl(params, `emby/Users/authenticatebyname`, endpoint)
  try {
    const response = await fetch(url, {
      headers: {
        "accept": "application/json",
        "accept-language": "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
        "content-type": "application/x-www-form-urlencoded; charset=UTF-8",
      },
      "referrerPolicy": "strict-origin-when-cross-origin",
      "body": `Username=${username}&Pw=${password}`,
      "method": "POST"
    })
    const text = await response.text()
    console.log(`login response`, text)
    console.log(`headers`, EMBY_CLIENT_HEADERS)
    const data = JSON.parse(text) as User
    return data
  } catch (e) {
    console.error(`login response`, e)
    return Promise.reject(new Error(`username or password is incorrect`))
  }
}