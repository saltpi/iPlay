import { OSType, isOS } from "@helper/device";
import { ComponentProps } from "react";
import NativeVideo, { VideoRef } from "react-native-video";
import { PlayerView } from "./Player";
import { StyleSheet } from "react-native";

const style = StyleSheet.create({
    player: {
        width: "100%",
        aspectRatio: 16/9,
    },
})

export type VideoProps = ComponentProps<typeof NativeVideo>;

export function Video(props: VideoProps) {
    if (isOS(OSType.Android)) {
        return <NativeVideo {...props} />;
    } else {
        const { uri } = props.source as any
        const onPlayStateChange = (s: any) => {
            const event = s.nativeEvent
            if (event.state === 5) {
                props.onPlaybackStateChanged?.({isPlaying: true})
            }
        }
        return (
            <PlayerView style={style.player}
                onPlayStateChange={onPlayStateChange}
                url={uri} />
        )
    }
}