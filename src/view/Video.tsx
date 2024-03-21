import {OSType, isOS} from '@helper/device';
import {
    ComponentProps,
    forwardRef,
    useCallback,
    useEffect,
    useImperativeHandle,
    useRef,
} from 'react';
import NativeVideo from 'react-native-video';
import { PlayerView } from './Player';
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
        const {uri} = props.source as any;
        const onPlayStateChange = (s: any) => {
            const event = s.nativeEvent;
            if (event.state === 5) {
                props.onPlaybackStateChanged?.({isPlaying: true});
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

        console.log(`ref`, nativeRef.current);

        return (
            <PlayerView
                style={style.player as any}
                ref={nativeRef}
                bgcolor="000000"
                onPlayStateChange={onPlayStateChange}
                url={uri}
            />
        );
    },
);

export const Video = isOS(OSType.Android) ? NativeVideo : VLCPlayer;
