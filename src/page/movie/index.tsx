import { PropsWithNavigation } from "@global";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { ActorCard } from "@view/ActorCard";
import { SeasonCardList } from "@view/SeasonCard";
import { Tag } from "@view/Tag";
import { ExternalPlayer } from "@view/player/ExternalPlayer";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { DimensionValue, ImageStyle, ScrollView, StyleProp, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Image } from '@view/Image';
import { Toast } from "@helper/toast";
import { Spin, SpinBox } from "@view/Spin";
import PlayIcon from "../../asset/play.svg"
import { getPlayUrl } from "@api/play";
import { Video } from "@view/Video";
import { preferedSize, windowWidth } from "@helper/device";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { logger, printException } from "@helper/log";
import { updatePlayerState } from "@store/playerSlice";
import { PlayEventType } from "@view/mpv/Player";
import { PlaybackStateType } from "@view/mpv/type";
import { fetchPlaybackAsync } from "@store/embySlice";
import { StatusBar } from "@view/StatusBar";
import { Like } from "@view/like/Like";
import { PlayCount } from "@view/counter/PlayCount";

const style = StyleSheet.create({
    overview: {
        padding: 5
    },
    actorSection: {
        marginTop: 5,
        marginLeft: 5,
        fontWeight: "600",
        fontSize: 17,
    },
    logo: {
        width: "50%", 
        height: 28, 
        marginTop: 10, 
        marginBottom: 10
    },
    tags: {
        flexDirection: "row",
        flexWrap: "wrap",
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
        width: 56,
        height: 56,
        aspectRatio: 1,
        overflow: "hidden",
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        borderRadius: 36,
        top: "50%",
        left: "50%",
        alignItems: "center",
        justifyContent: "center",
    },
    play: {
        flexGrow: 0,
        flexShrink: 0,
        aspectRatio: 1,
    },
    link: {
        textAlign: "center",
        color: "blue",
    },
    actionBar: {
        flex: 1,
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "center",
    },
    icon: {
        width: 24,
    }
})

export function Page({route, navigation}: PropsWithNavigation<"movie">) {
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const themeStyle = useAppSelector(selectThemeBasicStyle)
    const emby = useAppSelector(state => state.emby?.emby)
    const showVideoLink = useAppSelector(state => state.theme.showVideoLink)
    const dispatch = useAppDispatch()
    const {type, movie} = route.params
    const [url, setUrl] = useState<string>()
    const [isPlaying, setIsPlaying] = useState(false)
    const [detail, setDetail] = useState<MediaDetail>();
    const [seasons, setSeasons] = useState<Season[]>();
    const videoRef = useRef<any>()
    const [loading, setLoading] = useState(false)
    const [infoLoading, setInfoLoading] = useState(false)
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const subtitleFontName = useAppSelector(s => s.player.fontFamily)
    const poster = type==="Episode" ?
        emby?.imageUrl?.(movie.Id, null) :
        emby?.imageUrl?.(movie.Id, movie.BackdropImageTags?.[0], "Backdrop/0")

    const fetchPlayUrl = useCallback(async () => {
        let url = getPlayUrl(detail)
        console.log(`fetch play url`, url)
        if (!url || url?.length === 0) {
            const response = await dispatch(fetchPlaybackAsync(Number(movie.Id)))
            const playbackInfo = typeof response.payload !== "string" ? response.payload : null
            console.log(`playback info`, playbackInfo)
            if (playbackInfo) {
                url = emby?.videoUrl?.(playbackInfo) ?? ""
                console.log(`url from playback`, url)
                dispatch(updatePlayerState({
                    source: "emby",
                    mediaId: movie.Id,
                    mediaSourceId: playbackInfo.MediaSources[0].Id,
                    sessionId: playbackInfo.PlaySessionId,
                    startTime: Date.now(),
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
        return () => {
            logger.info("reset url")
            setUrl(undefined)
            dispatch(updatePlayerState({
                status: "stopped",
            }))
        }
    }, [movie.Id])

    useEffect(() => {
        setIsPlaying(false)
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
            status: "start",
            mediaName: detail?.Name,
            startTime: Date.now(),
        }))
    }

    const onPlaybackStateChanged = (data: PlaybackStateType) => {
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
    }
    const logoUrl = emby?.imageUrl?.(movie.Id, movie.BackdropImageTags?.[0], "Logo")
    const isPlayable = movie.Type === "Movie" || movie.Type === "Episode" 
    const iconSize = preferedSize(24, 56, windowWidth/9)
    const layout = useMemo(() => ({
        cover: {
            width: "100%" as DimensionValue, 
            height: windowWidth * 9/16 + pageStyle.paddingTop
        }
    }), [pageStyle.paddingTop, windowWidth])

    const playButtonStyle = {
        ...style.playButton,
        width: iconSize,
        height: iconSize,
        transform: [{translateX: -iconSize/2}, {translateY: -iconSize/2 + pageStyle.paddingTop/2}]
    }
    return (
        <ScrollView style={{backgroundColor}}
            showsVerticalScrollIndicator={false}>
            <StatusBar />
            <View>
            {url && isPlaying ? 
            <>
            <View style={{width: "100%", height: pageStyle.paddingTop}} />
            <Video
                ref={videoRef}
                subtitleFontName={subtitleFontName}
                source={{uri: url, title: detail?.Name ?? ""}}
                onPlaybackStateChanged={onPlaybackStateChanged}
                style={{...style.player}}
            />
            </>
             : null}
            {url && isPlaying ? null : 
            <Image style={layout.cover} source={{ uri: poster}} />
            }
            {isPlayable && !isPlaying ?
            <TouchableOpacity style={playButtonStyle} onPress={playVideo} activeOpacity={1.0}>
                <PlayIcon style={style.play} />
            </TouchableOpacity> : null}
            {loading ? <Spin color={themeStyle.color} size="small" /> : null}
            </View>
            <View style={style.actionBar}>
            <Image style={style.logo}
                resizeMode="contain"
                source={{uri: logoUrl}} />
            {detail ?
            <>
            <Like id={Number(movie.Id)}
                width={style.icon.width}
                isFavorite={detail?.UserData?.IsFavorite ?? false}
             />
            <PlayCount
                width={style.icon.width + 5}
                style={{color: themeStyle.color}}
                count={detail?.UserData?.PlayCount ?? 0} />
            </>
            : null }
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
            {detail?.People.length ? 
            <Text style={{...style.actorSection, ...themeStyle}}>
                演职人员
            </Text> 
            : null}
            <ScrollView horizontal 
                contentContainerStyle={{minWidth: "100%"}}
                showsHorizontalScrollIndicator={false}>
            {detail?.People.map((actor, index) => 
                <ActorCard key={index} 
                    theme={themeStyle} 
                    actor={actor}
                    onPress={() => navigation.navigate("actor", {title: actor.Name, actor})}
                />)}
            </ScrollView>
        </ScrollView>
    )
}