import { NativeModules } from "react-native";

export interface IntentModuleInterface {
    openUrl(url: string): void;
    playFile(filePath: string): void;
}

export const IntentModule = NativeModules.IntentModule as IntentModuleInterface