import { Storage } from 'redux-persist'
import { MMKV } from "react-native-mmkv"
import AsyncStorage from '@react-native-async-storage/async-storage';
import { OSType, isOS } from './device'



const storage = isOS(OSType.Windows) ? {
  set: (key: string, value: string) => AsyncStorage.setItem(key, value),
  getString: (key: string) => AsyncStorage.getItem(key),
  delete: (key: string) => AsyncStorage.removeItem(key),
} : new MMKV()


export const reduxStorage: Storage = {
  setItem: (key, value) => {
    storage.set(key, value)
    return Promise.resolve(true)
  },
  getItem: (key) => {
    const value = storage.getString(key)
    return Promise.resolve(value)
  },
  removeItem: (key) => {
    storage.delete(key)
    return Promise.resolve()
  },
}