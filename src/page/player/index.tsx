import {PropsWithNavigation} from '@global';
import { Toast } from '@helper/toast';
import { useAppSelector } from '@hook/store';
import { Episode } from '@model/Episode';
import { EpisodeCard } from '@view/EpisodeCard';
import {useEffect, useRef, useState} from 'react';
import {ScrollView, StyleSheet, View} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import Video, { VideoRef } from 'react-native-video';

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
        borderWidth: 0.5,
        borderRadius: 5
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
    const videoRef = useRef<VideoRef>(null);
    const onError = (e: any) => {
        console.log(`player: `, url, e);
        Toast.show({
            topOffset: insets.top,
            type: 'error',
            text2: JSON.stringify(e)
        })
        navigation.goBack()
    };
    const playEpisode = (episode: Episode) => {
        setEpisode(episode)
        setPoster(emby?.imageUrl?.(episode.Id, episode.ImageTags.Primary))
        emby?.getPlaybackInfo?.(Number(episode.Id))
            .then(res => {
                setUrl(emby?.videoUrl?.(res.MediaSources[0].Path))
                navigation.setOptions({
                    title: episode.Name
                })
            })
    }
    useEffect(() => {
        playEpisode(episode)
    }, [])
    return (
        <View style={style.root}>
        {url ? <Video
            source={{uri: url}}
            controls={true}
            poster={poster}
            fullscreenAutorotate={true}
            fullscreenOrientation="landscape"
            ref={videoRef}
            onError={onError}
            style={style.player}
        /> : null}
        <ScrollView>
        {episodes?.map((e, idx) => <EpisodeCard key={idx} 
            emby={emby} 
            style={e === episode ? style.playing : {}}
            onPress={playEpisode}
            episode={e} />)}
        </ScrollView>
        </View>
    );
}
