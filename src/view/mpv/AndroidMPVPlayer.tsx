import {forwardRef, useEffect, useImperativeHandle, useRef} from 'react';
import {
    ViewProps,
    requireNativeComponent,
} from 'react-native';
import { PlaybackStateType, VideoProps } from './type';

export interface MPVPlayerProps extends ViewProps {
    url?: string;
    title?: string
    onPlayStateChange?: any
}

export const MPVPlayer =
    requireNativeComponent<MPVPlayerProps>('PlayerViewManager');


export const AndroidMPVPlayerView = forwardRef<any, VideoProps>((props: VideoProps, ref) => {
    const nativeRef = useRef(null);
    const { source, ...rest } = props;

    const onPlayStateChange = (s: any) => {
        const state: PlaybackStateType = s.nativeEvent;
        props.onPlaybackStateChanged?.(state);
    };

    useImperativeHandle(
        ref,
        () => ({
            stop: () => null,
            resume: () => null,
            pause: () => null,
        }),
        [],
    );

    useEffect(() => {
    }, []);


    return <MPVPlayer ref={nativeRef} 
            title={source.title} 
            url={source.uri} 
            onPlayStateChange={onPlayStateChange}
            {...rest} />;
})
