import {forwardRef, useEffect, useRef} from 'react';
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


export const AndroidMPVPlayerView = forwardRef<any, VideoProps>((props: VideoProps) => {
    const ref = useRef(null);
    const { source, ...rest } = props;

    const onPlayStateChange = (s: any) => {
        const state: PlaybackStateType = s.nativeEvent;
        props.onPlaybackStateChanged?.(state);
    };

    useEffect(() => {
    }, []);


    return <MPVPlayer ref={ref} 
            title={source.title} 
            url={source.uri} 
            onPlayStateChange={onPlayStateChange}
            {...rest} />;
})
