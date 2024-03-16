import AsyncStorage from "@react-native-async-storage/async-storage";

export async function set(key: string, value: any) {
    return await AsyncStorage.setItem(key, value)
}

export async function get(key: string) {
    return await AsyncStorage.getItem(key)
}

export const Store = {
    set,
    get
}