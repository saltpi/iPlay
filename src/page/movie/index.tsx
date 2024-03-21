import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { ActorCard } from "@view/ActorCard";
import { SeasonCardList } from "@view/SeasonCard";
import { Tag } from "@view/Tag";
import { ExternalPlayer } from "@view/player/ExternalPlayer";
import { useCallback, useEffect, useRef, useState } from "react";
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { BaseImage, Image } from '@view/Image';
import { VideoRef } from "react-native-video";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Spin } from "@view/Spin";
import PlayIcon from "../../asset/play.svg"
import { getPlayUrl } from "@api/play";
import { Video } from "@view/Video";

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
        width: 36,
        height: 36,
        aspectRatio: 1,
        overflow: "hidden",
        top: "50%",
        left: "50%",
        transform: [{translateX: -25}, {translateY: -25}],
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
    }
})

export function Page({route}: PropsWithNavigation<"movie">) {
    const emby = useAppSelector(state => state.emby?.emby)
    const {title, type, movie} = route.params
    const [url, setUrl] = useState<string>()
    const [isPlaying, setIsPlaying] = useState(false)
    const [detail, setDetail] = useState<MediaDetail>();
    const [seasons, setSeasons] = useState<Season[]>();
    const videoRef = useRef<any>()
    const [loading, setLoading] = useState(false)
    const poster = type==="Episode" ?
        emby?.imageUrl?.(movie.Id, null) :
        emby?.imageUrl?.(movie.Id, movie.BackdropImageTags?.[0], "Backdrop/0")
    const insets = useSafeAreaInsets()

    useEffect(() => {
        return () => {
            console.log(`unmount`, videoRef)
            videoRef.current?.stop?.()
        }
    }, [])

    useEffect(() => {
        emby?.getMedia?.(Number(movie.Id)).then(setDetail)
        if (type !== "Series") return
        emby?.getSeasons?.(Number(movie.Id)).then(setSeasons)
    }, [emby, movie.Id])
    
    const onError = (e: any) => {
        console.log(`player: `, url, e);
        setLoading(false)
        Toast.show({
            topOffset: insets.top,
            type: 'error',
            text2: JSON.stringify(e)
        })
    }

    const playVideo = async () => {
        let url = getPlayUrl(detail)
        if (!url || url?.length === 0) {
            const playbackInfo = await emby?.getPlaybackInfo?.(Number(movie.Id))
            if (playbackInfo) {
                url = emby?.videoUrl?.(playbackInfo) ?? ""
            }
            console.log("playbackInfo", url)
        }
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
    }
    const isPlayable = movie.Type === "Movie" || movie.Type === "Episode" 
    return (
        <ScrollView>
            <View>
            {url ? <Video
                ref={videoRef}
                source={{uri: url}}
                controls={true}
                poster={poster}
                fullscreenAutorotate={true}
                fullscreenOrientation="landscape"
                onPlaybackStateChanged={state => setLoading(false)}
                onProgress={progress => console.log("progress", progress)}
                onError={onError}
                style={style.player}
            /> : null}
            {url ? null : <Image style={{width: "100%", aspectRatio: 16/9}} source={{ uri: poster}} />}
            {isPlayable ?
            <TouchableOpacity style={style.playButton} onPress={playVideo} activeOpacity={1.0}>
            {isPlaying ? null : <PlayIcon width={50} height={50} style={style.play} />}
            </TouchableOpacity> : null}
            {/* {loading ? <Spin size="small" /> : null} */}
            </View>
            <View style={style.tags}>
                {detail?.Genres.map((genre, index) => <Tag key={index}>{genre}</Tag>)}
            </View>
            <Text style={style.overview}>{detail?.Overview}</Text>
            {isPlayable ?
            <ExternalPlayer title={detail?.Name} src={getPlayUrl(detail)} /> : null}
            {seasons ? <SeasonCardList seasons={seasons} /> : null}
            {detail?.People.length ? <Text style={style.actorSection}>演职人员</Text> : null}
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
            {detail?.People.map((actor, index) => <ActorCard key={index} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}