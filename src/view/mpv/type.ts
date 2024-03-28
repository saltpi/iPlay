import { ViewProps } from "react-native";

export type VideoProps = {
    source: {
        uri: string;
        title: string;
    };
    onPlaybackStateChanged?: (state: {isPlaying: boolean}) => void;
} & ViewProps