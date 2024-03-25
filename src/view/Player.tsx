import React, { forwardRef } from 'react';
import {ViewProps, requireNativeComponent} from 'react-native';
const Player = requireNativeComponent('PlayerView');

export enum PlayStateType {
    PlayEventTypeOnProgress,
    PlayEventTypeOnPause,
    PlayEventTypeOnPauseForCache,
    PlayEventTypeDuration,
    PlayEventTypeEnd,
}

export interface PlayerViewProps extends ViewProps {
    title?: string
    onPlayStateChange?: (state: PlayStateType) => void
    url: string
    iconSize?: number
}

export const PlayerView = forwardRef<any, PlayerViewProps>((props, ref) => {
    return <Player {...props} ref={ref} />;
})