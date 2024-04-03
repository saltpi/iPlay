import { reduxStorage } from "./storage";

export async function set(key: string, value: any) {
    return await reduxStorage.setItem(key, value)
}

export async function get(key: string) {
    return await reduxStorage.getItem(key)
}

export const StorageHelper = {
    set,
    get
}