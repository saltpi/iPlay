import {PropsWithNavigation} from '@global';
import { Device } from '@helper/device';
import { printException } from '@helper/log';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { Episode } from "@model/Episode";
import { updatePlayerState } from '@store/playerSlice';
import { selectThemeBasicStyle, selectThemedPageStyle } from '@store/themeSlice';
import { EpisodeCard } from '@view/EpisodeCard';
import { Image } from '@view/Image';
import { Spin } from '@view/Spin';
import { Video } from '@view/Video';
import { PlayEventType } from '@view/mpv/Player';
import { PlaybackStateType } from '@view/mpv/type';
import { ExternalPlayer } from '@view/player/ExternalPlayer';
import {useCallback, useEffect, useMemo, useRef, useState} from 'react';
import {ScrollView, StyleSheet, Text, View} from 'react-native';

// Later on in your styles..
const style = StyleSheet.create({
    root: {
        flex: 1
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
    },
    padRoot: {
        flexDirection: "row"
    },
    padContainer: {
        width: "60%"
    },
    player: {
        width: "100%",
        aspectRatio: 16/9,
    },
    overview: {
        padding: 10,
    }
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
    const theme = useAppSelector(selectThemeBasicStyle)
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const subtitleFontName = useAppSelector(s => s.player.fontFamily)
    const dispatch = useAppDispatch()
    const isTablet = Device.isTablet

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

    const layout = useMemo(() => ({
        page: {
            ...style.root, 
            ...(isTablet ? style.padRoot : {})
        },
        player: {
            ...style.playerContainer, 
            ...(isTablet ? style.padContainer : {})
        },
        playlist: {
            backgroundColor: theme.backgroundColor, 
            marginTop: isTablet ? pageStyle.paddingTop : 0,
        },
        overview: {
            ...style.overview,
            ...theme
        }
    }), [isTablet, theme, pageStyle])


    return (
        <View style={layout.page}>
        <View style={layout.player}>
            <>
            <View style={{width: "100%", height: pageStyle.paddingTop}} />
            {url ? 
            <Video
                ref={videoRef}
                source={{uri: url, title: episode.Name}}
                subtitleFontName={subtitleFontName}
                onPlaybackStateChanged={onPlaybackStateChanged}
                style={style.player}
            />
            :
            <Image style={style.player} source={{uri: poster}} />
            }
            </>
            {loading ? <Spin color={theme.color} /> : null}
            {isTablet && url ? <ExternalPlayer src={url} title={episode.Name} /> : null}
            {isTablet ? <Text style={layout.overview}>{episode.Overview}</Text> : null}
        </View>
        <ScrollView style={layout.playlist}
            showsVerticalScrollIndicator={false}>
        {!isTablet && url ? <ExternalPlayer src={url} title={episode.Name} /> : null}
        {episodes?.map((e, idx) => <EpisodeCard key={idx} 
            emby={emby} 
            theme={theme}
            style={e === episode ? style.playing : style.inactive}
            onPress={playEpisode}
            episode={e} />)}
        </ScrollView>
        </View>
    );
}
