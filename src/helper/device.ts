import { EMBY_CLIENT_HEADERS } from "@api/view";
import { random } from "lodash";
import { Platform } from "react-native";
import { Dimensions } from 'react-native';
import DeviceInfo, { isTablet } from "react-native-device-info";

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
    did: random(1000, 9999).toString(),
    // safe area insets
    insets: { top: 0, right: 0, bottom: 0, left: 0 },
    isTablet: isTablet() || windowWidth > windowHeight,
    isWindows: isOS(OSType.Windows),
    isMacOS: isOS(OSType.macOS),
    isWeb: isOS(OSType.Web),
    isAndroid: isOS(OSType.Android),
    isIOS: isOS(OSType.iOS),
    isMobile: isOS(OSType.iOS) || isOS(OSType.Android),
    isDesktop: isOS(OSType.Windows) || isOS(OSType.macOS),

    init: async () => {
        Device.did = await DeviceInfo.getUniqueId();
        Device.name = await DeviceInfo.getDeviceName();
        EMBY_CLIENT_HEADERS["X-Emby-Device-Id"] = Device.did;
        EMBY_CLIENT_HEADERS["X-Emby-Device-Name"] = Device.name;
        console.log("device info", Device.name, Device.did)
    }
}