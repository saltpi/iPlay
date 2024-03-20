import React from 'react';
import {requireNativeComponent} from 'react-native';
import PropTypes from 'prop-types';
const Player = requireNativeComponent('Player');

class PlayerView extends React.Component {
    static propTypes: {
        /**
         * A Boolean value that determines whether the user may use pinch
         * gestures to zoom in and out of the map.
         */
        bgcolor: any;
        url: any;
    };
    render() {
        return <Player {...this.props} />;
    }
}

PlayerView.propTypes = {
    /**
     * A Boolean value that determines whether the user may use pinch
     * gestures to zoom in and out of the map.
     */
    bgcolor: PropTypes.string,
    url: PropTypes.string,
};

// requireNativeComponent automatically resolves 'RNTMap' to 'RNTMapManager'
module.exports = PlayerView;
