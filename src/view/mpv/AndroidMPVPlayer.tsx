import {useEffect, useRef} from 'react';
import {
    UIManager,
    ViewProps,
    findNodeHandle,
    requireNativeComponent,
} from 'react-native';
import { VideoProps } from './type';

export interface MPVPlayerProps extends ViewProps {
    url?: string;
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

export function AndroidMPVPlayerView(props: VideoProps) {
    const ref = useRef(null);
    const { source, ...rest } = props;

    useEffect(() => {
        const viewId = findNodeHandle(ref.current);
        if (viewId) createFragment(viewId);
    }, []);

    return <MPVPlayer ref={ref} url={source.uri} {...rest} />;
}
