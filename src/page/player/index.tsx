import {PropsWithNavigation} from '@global';
import { printException } from '@helper/log';
import { Toast } from '@helper/toast';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { Episode } from "@model/Episode";
import { updatePlayerState } from '@store/playerSlice';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { EpisodeCard } from '@view/EpisodeCard';
import { Spin } from '@view/Spin';
import { Video } from '@view/Video';
import { PlayEventType } from '@view/mpv/Player';
import { PlaybackStateType } from '@view/mpv/type';
import { ExternalPlayer } from '@view/player/ExternalPlayer';
import {useCallback, useEffect, useRef, useState} from 'react';
import {ScrollView, StyleSheet, View} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

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
    const { episodes } = route.params;
    const emby = useAppSelector(state => state.emby.emby);
    const [url, setUrl] = useState<string>()
    const [poster, setPoster] = useState<string>()
    const [episode, setEpisode] = useState(route.params.episode)
    const [loading, setLoading] = useState(true)
    const videoRef = useRef<any>(null);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)
    const pagePaddingTop = useAppSelector(state => state.theme.pagePaddingTop)
    const dispatch = useAppDispatch()

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
                dispatch(updatePlayerState({
                    source: "emby",
                    status: "start",
                    mediaId: episode.Id,
                    mediaSourceId: res.MediaSources[0]?.Id,
                    sessionId: res.PlaySessionId,
                    startTime: Date.now(),
                    mediaPoster: poster,
                    position: 0,
                }))
            })
            .catch(printException)
    }

    const onPlaybackStateChanged = useCallback((data: PlaybackStateType) => {
        if (data.type === PlayEventType.PlayEventTypeOnProgress) {
            setLoading(false)
            dispatch(updatePlayerState({
                status: "playing",
                mediaEvent: "TimeUpdate",
                position: data.position,
                duration: data.duration,
            }))
        } else if (data.type === PlayEventType.PlayEventTypeOnPause) {
            console.log("player paused")
            dispatch(updatePlayerState({
                status: "paused",
                mediaEvent: "Pause",
                isPaused: true,
            }))
        }
    }, [dispatch, setLoading])

    useEffect(() => {
        playEpisode(episode)
        return () => {
            console.log("stop player")
            dispatch(updatePlayerState({
                status: "stopped",
            }))
        }
    }, [])

    return (
        <View style={{...style.root, paddingTop: pagePaddingTop}}>
        <View style={style.playerContainer}>
            {url ? <Video
                source={{uri: url, title: episode.Name}}
                ref={videoRef}
                onPlaybackStateChanged={onPlaybackStateChanged}
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
