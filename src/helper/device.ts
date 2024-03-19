import { Platform } from "react-native";

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