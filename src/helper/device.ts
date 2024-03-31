import { random } from "lodash";
import { Platform } from "react-native";
import { Dimensions } from 'react-native';
import DeviceInfo from "react-native-device-info";
import { initialWindowMetrics } from "react-native-safe-area-context";

export enum OSType {
    Web = "web",
    iOS = "ios",
    Android = "android",
    macOS = "macos",
    Windows = "windows",
}

export function isOS(type: OSType) {
    return Platform.OS === type;
}

export function isWeb() {
    Platform.OS === "web";
}

export function isIOS() {
    Platform.OS === "ios";
}

export function isAndroid() {
    Platform.OS === "android";
}


export const windowWidth = Dimensions.get('window').width;
export const screenWidth = Dimensions.get("screen").width;
export const windowHeight = Dimensions.get('window').height;
export const screenHeight = Dimensions.get("screen").height;

export const preferedSize = (start: number, end: number, value: number) => {
    if (value < start) {
        return start;
    }
    if (value > end) {
        return end;
    }
    return value;
}

export const Version = {
    displayName: "iPlay",
    buildNumber: DeviceInfo.getBuildNumber(),
    versionCode: DeviceInfo.getVersion(),
    deviceId: DeviceInfo.getDeviceId(),
}

export const Device = {
    name: "",
    did: random(1000000000000000, 9999999999999999).toString(),
    // safe area insets
    insets: { top: 0, right: 0, bottom: 0, left: 0 },

    init: async () => {
        Device.did = await DeviceInfo.getUniqueId();
        Device.name = await DeviceInfo.getDeviceName();
        console.log("device info", Device.name, Device.did)
    }
}