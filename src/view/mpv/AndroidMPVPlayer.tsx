import {forwardRef, useEffect, useRef} from 'react';
import {
    UIManager,
    ViewProps,
    findNodeHandle,
    requireNativeComponent,
} from 'react-native';
import { VideoProps } from './type';
import { PlayStateType } from './Player';

export interface MPVPlayerProps extends ViewProps {
    url?: string;
    title?: string
    onPlayStateChange?: any
}

export const MPVPlayer =
    requireNativeComponent<MPVPlayerProps>('PlayerViewManager');

const createFragment = (viewId: number) => {
    UIManager.dispatchViewManagerCommand(
        viewId,
        // we are calling the 'create' command
        (UIManager as any).PlayerViewManager.Commands.create.toString(),
        [viewId],
    )
}

export const AndroidMPVPlayerView = forwardRef<any, VideoProps>((props: VideoProps, vRef: any) => {
    const ref = useRef(null);
    const { source, ...rest } = props;

    const onPlayStateChange = (s: any) => {
        const state = s.nativeEvent?.state;
        if (state === PlayStateType.PlayEventTypeOnPause) {
            props.onPlaybackStateChanged?.({isPlaying: false});
        } else if (state === PlayStateType.PlayEventTypeOnProgress) {
            props.onPlaybackStateChanged?.({isPlaying: true});
        } else if (state === PlayStateType.PlayEventTypeEnd) {
            props.onPlaybackStateChanged?.({isPlaying: false});
        }
    };

    useEffect(() => {
        const viewId = findNodeHandle(ref.current);
        if (viewId) createFragment(viewId);
        () => {
            console.log("destroy");
            UIManager.dispatchViewManagerCommand(
                viewId,
                // we are calling the 'destroy' command
                (UIManager as any).PlayerViewManager.Commands.destroy.toString(),
                [viewId],
            )
        }
    }, []);


    return <MPVPlayer ref={ref} 
            title={source.title} 
            url={source.uri} 
            onPlayStateChange={onPlayStateChange}
            {...rest} />;
})
