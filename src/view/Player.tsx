import React, { forwardRef } from 'react';
import {requireNativeComponent} from 'react-native';
const Player = requireNativeComponent('PlayerView');

export const PlayerView = forwardRef((props, ref) => {
    return <Player {...props} ref={ref} />;
})