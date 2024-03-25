import {OSType, isOS, preferedSize, windowWidth} from '@helper/device';
import {
    ComponentProps,
    forwardRef,
    useCallback,
    useEffect,
    useImperativeHandle,
    useRef,
} from 'react';
import NativeVideo from 'react-native-video';
import { PlayStateType, PlayerView } from './Player';
import {NativeModules, StyleSheet, findNodeHandle} from 'react-native';

export interface PlayerManagerType {
    resume: (reactTag: number) => void;
    pause: (reactTag: number) => void;
    stop: (reactTag: number) => void;
}

export interface PlayerRef {
    resume: (reactTag: number) => void;
    pause: (reactTag: number) => void;
    stop: (reactTag: number) => void;
}
const PlayerManager = NativeModules.PlayerView as PlayerManagerType;

const style = StyleSheet.create({
    player: {
        width: '100%',
        aspectRatio: 16 / 9,
    },
});

export type VideoProps = ComponentProps<typeof NativeVideo>;

export const VLCPlayer = forwardRef<PlayerRef, VideoProps>(
    (props, ref) => {
        const nativeRef = useRef(null);
        const {uri, title} = props.source as any;
        console.log(`uri: ${uri} title: ${title}`);
        const onPlayStateChange = (s: any) => {
            const state = s.nativeEvent?.state;
            console.log(`onPlayStateChange: ${state}`);
            if (state === PlayStateType.PlayEventTypeOnPause) {
                props.onPlaybackStateChanged?.({isPlaying: false});
            } else if (state === PlayStateType.PlayEventTypeOnProgress) {
                props.onPlaybackStateChanged?.({isPlaying: true});
            } else if (state === PlayStateType.PlayEventTypeEnd) {
                props.onPlaybackStateChanged?.({isPlaying: false});
            }
        };

        const stop = useCallback(() => {
            const id = findNodeHandle(nativeRef.current);
            console.log(`call stop: ${id} ${nativeRef}`);
            PlayerManager.stop(id!);
        }, []);


        useImperativeHandle(
            ref,
            () => ({
                stop: () => null,
                resume: () => null,
                pause: () => null,
            }),
            [stop],
        );

        return (
            <PlayerView
                style={style.player as any}
                iconSize={preferedSize(25, 48, windowWidth/10)}
                ref={nativeRef}
                title={title}
                onPlayStateChange={onPlayStateChange}
                url={uri}
            />
        );
    },
);

export const Video = isOS(OSType.Android) ? NativeVideo : VLCPlayer;
