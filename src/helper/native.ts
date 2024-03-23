import { NativeModules } from "react-native";

export interface IntentModuleInterface {
    openUrl(url: string): void;
}

export const IntentModule = NativeModules.IntentModule as IntentModuleInterface