import React, { forwardRef } from 'react';
import {ViewProps, requireNativeComponent} from 'react-native';
const Player = requireNativeComponent('PlayerView');

export interface PlayerViewProps extends ViewProps {
    title?: string
    onPlayStateChange?: (state: number) => void
    url: string
}

export const PlayerView = forwardRef<any, PlayerViewProps>((props, ref) => {
    return <Player {...props} ref={ref} />;
})