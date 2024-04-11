import { REACT_APP_MODE, REACT_APP_TEST_VIDEO_URL } from "@env"
import { NativeModules } from "react-native";

NativeModules.DevSettings.setIsDebuggingRemotely(false);

export const Dev = {
    mode: REACT_APP_MODE,
    videoUrl: REACT_APP_TEST_VIDEO_URL
}