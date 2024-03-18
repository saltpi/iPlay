import {PropsWithNavigation} from '@global';
import {useEffect, useRef} from 'react';
import {StyleSheet} from 'react-native';
import Video from 'react-native-video';

// Later on in your styles..
const styles = StyleSheet.create({
    backgroundVideo: {
        position: 'absolute',
        top: 0,
        left: 0,
        bottom: 0,
        right: 0,
        backgroundColor: 'black',
    },
});

export type PlayerPageProps = PropsWithNavigation<'player'>;

export function Page({navigation, route}: PlayerPageProps) {
    const {title, poster, media} = route.params;
    const videoRef = useRef<Video>(null);
    const url = media.MediaSources[0].Path;
    useEffect(() => {
        navigation.setOptions({
            title,
            headerShown: false,
        });
    }, [])
    return (
        <Video
            // Can be a URL or a local file.
            source={{uri: url}}
            controls={true}
            poster={poster}
            fullscreen={true}
            fullscreenAutorotate={true}
            ref={videoRef}
            style={styles.backgroundVideo}
        />
    );
}
