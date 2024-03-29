import { ViewProps } from "react-native";
import { PlayEventType } from "./Player";

export type PlaybackStateType = {
    isPlaying: boolean,
    type: PlayEventType,
    position?: number,
    duration?: number,
}
export type VideoProps = {
    source: {
        uri: string;
        title: string;
    };
    onPlaybackStateChanged?: (state: PlaybackStateType) => void;
} & ViewProps