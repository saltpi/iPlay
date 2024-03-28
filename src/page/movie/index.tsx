import { PropsWithNavigation } from "@global";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { ActorCard } from "@view/ActorCard";
import { SeasonCardList } from "@view/SeasonCard";
import { Tag } from "@view/Tag";
import { ExternalPlayer } from "@view/player/ExternalPlayer";
import { useCallback, useEffect, useRef, useState } from "react";
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Image } from '@view/Image';
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Spin, SpinBox } from "@view/Spin";
import PlayIcon from "../../asset/play.svg"
import { getPlayUrl } from "@api/play";
import { Video } from "@view/Video";
import { preferedSize, windowWidth } from "@helper/device";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { printException } from "@helper/log";
import { updatePlayerState } from "@store/playerSlice";
import { kSecond2TickScale } from "@model/PlaybackData";
import { PlayEventType } from "@view/mpv/Player";

const style = StyleSheet.create({
    overview: {
        padding: 5
    },
    actorSection: {
        marginTop: 5,
        fontWeight: "600",
        fontSize: 20,
    },
    tags: {
        flexDirection: "row",
        marginTop: 5,
        paddingLeft: 2.5,
        paddingRight: 2.5,
    },
    player: {
        width: "100%",
        aspectRatio: 16/9,
    },
    playButton: {
        position: "absolute",
        width: 72,
        height: 72,
        aspectRatio: 1,
        overflow: "hidden",
        backgroundColor: "rgba(255, 255, 255, 0.5)",
        borderRadius: 36,
        top: "50%",
        left: "50%",
        transform: [{translateX: -36}, {translateY: -36}],
        alignItems: "center",
        justifyContent: "center",
    },
    play: {
        flexGrow: 0,
        flexShrink: 0,
        width: 50,
        height: 50,
        aspectRatio: 1,
        tintColor: "white",
        color: "white",
    },
    link: {
        textAlign: "center",
        color: "blue",
    }
})

export function Page({route}: PropsWithNavigation<"movie">) {
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const themeStyle = useAppSelector(selectThemeBasicStyle)
    const emby = useAppSelector(state => state.emby?.emby)
    const showVideoLink = useAppSelector(state => state.theme.showVideoLink)
    const dispatch = useAppDispatch()
    const {title, type, movie} = route.params
    const [url, setUrl] = useState<string>()
    const [isPlaying, setIsPlaying] = useState(false)
    const [detail, setDetail] = useState<MediaDetail>();
    const [seasons, setSeasons] = useState<Season[]>();
    const videoRef = useRef<any>()
    const [loading, setLoading] = useState(false)
    const [infoLoading, setInfoLoading] = useState(false)
    const poster = type==="Episode" ?
        emby?.imageUrl?.(movie.Id, null) :
        emby?.imageUrl?.(movie.Id, movie.BackdropImageTags?.[0], "Backdrop/0")
    const insets = useSafeAreaInsets()

    const fetchPlayUrl = useCallback(async () => {
        let url = getPlayUrl(detail)
        if (!url || url?.length === 0) {
            const playbackInfo = await emby?.getPlaybackInfo?.(Number(movie.Id))
            if (playbackInfo) {
                url = emby?.videoUrl?.(playbackInfo) ?? ""
                dispatch(updatePlayerState({
                    source: "emby",
                    mediaId: movie.Id,
                    sessionId: playbackInfo.PlaySessionId,
                    startTime: Date.now() * kSecond2TickScale,
                    mediaPoster: poster,
                    position: 0,
                }))
            }
        }
        return url
    }, [emby, movie.Id])

    useEffect(() => {
        if (movie.Type === "Series") return
        fetchPlayUrl()
            .then(setUrl)
            .catch(printException)
    }, [])

    useEffect(() => {
        setInfoLoading(true)
        emby?.getMedia?.(Number(movie.Id))
            .then(setDetail)
            .catch(printException)
            .finally(() => setInfoLoading(false))

        if (type !== "Series") return
        emby?.getSeasons?.(Number(movie.Id))
            .then(setSeasons)
            .catch(printException)
    }, [emby, movie.Id])
    
    const onError = (e: any) => {
        setLoading(false)
        Toast.show({
            topOffset: insets.top,
            type: 'error',
            text2: JSON.stringify(e)
        })
    }

    const playVideo = async () => {
        if (!url || url?.length === 0) {
            Toast.show({
                type: "error",
                text1: "无法播放",
                text2: "没有找到可播放的资源"
            })
            return
        }
        setUrl(url)
        setIsPlaying(true)
        setLoading(true)
        dispatch(updatePlayerState({
            status: "playing",
            mediaName: detail?.Name,
        }))
    }

    const onPlaybackStateChanged = (data: any) => {
        setLoading(false)
        if (data.type === PlayEventType.PlayEventTypeOnProgress) {
            dispatch(updatePlayerState({
                mediaEvent: "timeupdate",
                position: data.position,
                duration: data.duration,
            }))
        } else if (data.type === PlayEventType.PlayEventTypeOnPause) {
            dispatch(updatePlayerState({
                mediaEvent: "pause",
                isPaused: true,
            }))
        }
    }

    const isPlayable = movie.Type === "Movie" || movie.Type === "Episode" 
    const iconSize = preferedSize(24, 36, windowWidth/10)
    const playButtonStyle = {
        ...style.playButton,
        width: iconSize,
        height: iconSize,
        transform: [{translateX: -iconSize/2}, {translateY: -iconSize/2}]
    }
    return (
        <ScrollView style={{backgroundColor}}
            showsVerticalScrollIndicator={false}>
            <View>
            {url && isPlaying ? <Video
                ref={videoRef}
                source={{uri: url, title: detail?.Name}}
                controls={true}
                poster={poster}
                fullscreenAutorotate={true}
                fullscreenOrientation="landscape"
                onPlaybackStateChanged={onPlaybackStateChanged}
                onProgress={progress => console.log("progress", progress)}
                onError={onError}
                style={style.player}
            /> : null}
            {url && isPlaying ? null : <Image style={{width: "100%", aspectRatio: 16/9}} source={{ uri: poster}} />}
            {isPlayable && !isPlaying ?
            <TouchableOpacity style={playButtonStyle} onPress={playVideo} activeOpacity={1.0}>
                <PlayIcon width={playButtonStyle.width/2} height={playButtonStyle.height/2} style={style.play} />
            </TouchableOpacity> : null}
            {loading ? <Spin color={themeStyle.color} size="small" /> : null}
            </View>
            <View style={style.tags}>
                {detail?.Genres.map((genre, index) => <Tag key={index}>{genre}</Tag>)}
            </View>
            {infoLoading ? 
            <SpinBox color={themeStyle.color} size="small" /> 
            : null}
            <Text style={{...style.overview, color}}>{detail?.Overview}</Text>
            {isPlayable && url ?
            <ExternalPlayer title={detail?.Name} src={url} /> : null}
            {showVideoLink ? 
            <Text style={{...style.link, ...themeStyle}}
                ellipsizeMode="tail" 
                numberOfLines={3}>
                {url}
            </Text> : null}
            {seasons ? <SeasonCardList seasons={seasons} /> : null}
            {detail?.People.length ? <Text style={{...style.actorSection, ...themeStyle}}>
                演职人员
            </Text> : null}
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {detail?.People.map((actor, index) => <ActorCard key={index} theme={themeStyle} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}