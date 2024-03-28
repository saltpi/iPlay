import {forwardRef, useEffect, useImperativeHandle, useRef} from 'react';
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

export const RealDemoView =
    requireNativeComponent<ViewProps>('DemoView');

export const DemoView = forwardRef<any,ViewProps>((props: ViewProps, extRef) => {
    const ref = useRef<any>()
    useImperativeHandle(extRef, () => ref.current, [ref.current])
    useEffect(() => {
        console.log("mount video view")
        return () => {
            console.log("unmount video view", ref)
        }
    }, [])
    return <RealDemoView ref={ref} {...props} />;
})

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
    }, []);


    return <MPVPlayer ref={ref} 
            title={source.title} 
            url={source.uri} 
            onPlayStateChange={onPlayStateChange}
            {...rest} />;
})
