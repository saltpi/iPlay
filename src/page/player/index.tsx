import {PropsWithNavigation} from '@global';
import { printException } from '@helper/log';
import { Toast } from '@helper/toast';
import { useAppSelector } from '@hook/store';
import { Episode } from '@model/Episode';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { EpisodeCard } from '@view/EpisodeCard';
import { Spin } from '@view/Spin';
import { Video } from '@view/Video';
import { ExternalPlayer } from '@view/player/ExternalPlayer';
import {useEffect, useRef, useState} from 'react';
import {ScrollView, StyleSheet, View} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { VideoRef } from 'react-native-video';

// Later on in your styles..
const style = StyleSheet.create({
    root: {
        position: 'absolute',
        top: 0,
        left: 0,
        bottom: 0,
        right: 0,
    },
    playing: {
        borderColor: "red",
        borderWidth: 2,
        borderRadius: 5,
        marginTop: 2.5,
        marginLeft: 2.5,
        marginRight: 2.5,
    },
    inactive: {
        borderColor: "transparent",
        borderWidth: 2,
        borderRadius: 5,
        marginTop: 2.5,
        marginLeft: 2.5,
        marginRight: 2.5,
    },
    goback: {
        position: 'absolute',
        top: "5%",
        left: "5%",
        backgroundColor: "red",
        zIndex: 10,
        flexGrow: 0,
        flexShrink: 0,
    },
    playerContainer: {
        width: "100%",
        aspectRatio: 16/9,
    },
    player: {
        width: "100%",
        aspectRatio: 16/9,
    },
});

export type PlayerPageProps = PropsWithNavigation<'player'>;

export function Page({navigation, route}: PlayerPageProps) {
    const insets = useSafeAreaInsets()
    const { episodes } = route.params;
    const emby = useAppSelector(state => state.emby.emby);
    const [url, setUrl] = useState<string>()
    const [poster, setPoster] = useState<string>()
    const [episode, setEpisode] = useState(route.params.episode)
    const [loading, setLoading] = useState(true)
    const videoRef = useRef<VideoRef>(null);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)
    const onError = (e: any) => {
        Toast.show({
            topOffset: insets.top,
            type: 'error',
            text2: JSON.stringify(e)
        })
        navigation.goBack()
    };

    const playEpisode = (episode: Episode) => {
        setLoading(true)
        setEpisode(episode)
        setPoster(emby?.imageUrl?.(episode.Id, episode.ImageTags.Primary))
        emby?.getPlaybackInfo?.(Number(episode.Id))
            .then(res => {
                setUrl(emby?.videoUrl?.(res))
                navigation.setOptions({
                    title: episode.Name
                })
            })
            .catch(printException)
    }
    useEffect(() => {
        playEpisode(episode)
    }, [])
    return (
        <View style={style.root}>
        <View style={style.playerContainer}>
            {url ? <Video
                source={{uri: url, title: episode.Name}}
                controls={true}
                poster={poster}
                fullscreenAutorotate={true}
                fullscreenOrientation="landscape"
                ref={videoRef}
                onProgress={() => setLoading(false)}
                onError={onError}
                style={style.player}
            /> : null}
            {loading ? <Spin color={theme.color} /> : null}
        </View>
        <ScrollView style={{backgroundColor}}>
        {url ? <ExternalPlayer src={url} title={episode.Name} /> : null}
        {episodes?.map((e, idx) => <EpisodeCard key={idx} 
            emby={emby} 
            style={e === episode ? style.playing : style.inactive}
            onPress={playEpisode}
            episode={e} />)}
        </ScrollView>
        </View>
    );
}
