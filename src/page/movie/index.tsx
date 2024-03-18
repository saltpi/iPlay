import { Api } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { ActorCard } from "@view/ActorCard";
import { SeasonCardList } from "@view/SeasonCard";
import { Tag } from "@view/Tag";
import { ExternalPlayer } from "@view/player/ExternalPlayer";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, Touchable, TouchableOpacity, View } from "react-native";
import { Image } from '@view/Image';
import Video, { VideoRef } from "react-native-video";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";

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
        alignItems: "center",
        justifyContent: "center",
    },
    play: {
        borderWidth: 1,
        borderColor: "black",
        color: "white",
        overflow: "hidden",
        backgroundColor: "black",
        borderRadius: 9,
        flexGrow: 0,
        flexShrink: 0,
        fontSize: 20,
        padding: 10,
        width: "auto",
        marginTop: 5,
        marginBottom: 5,
    }
})

export function Page({route}: PropsWithNavigation<"movie">) {
    const emby = useAppSelector(state => state.emby?.emby)
    const {title, type, movie} = route.params
    const [url, setUrl] = useState<string>()
    const [detail, setDetail] = useState<MediaDetail>();
    const [seasons, setSeasons] = useState<Season[]>();
    const [videoRef, setVideoRef] = useState<VideoRef>()
    const poster = emby?.imageUrl?.(movie.Id, movie.BackdropImageTags[0], "Backdrop/0")
    const insets = useSafeAreaInsets()
    useEffect(() => {
        emby?.getMedia?.(Number(movie.Id)).then(setDetail)
        if (type !== "Series") return
        emby?.getSeasons?.(Number(movie.Id)).then(setSeasons)
    }, [emby, movie.Id])
    const onError = (e: any) => {
        console.log(`player: `, url, e);
        Toast.show({
            topOffset: insets.top,
            type: 'error',
            text2: JSON.stringify(e)
        })
    }
    const getPlayUrl = (detail?: MediaDetail) => {
        const sources = detail?.MediaSources ?? []
        if (sources.length > 0) {
            const source = sources[0]
            if (source.Container === "strm") {
                return source.Path
            } else {
                return source.DirectStreamUrl
            }
        }
        return ""
    }
    const playVideo = () => {
        const url = getPlayUrl(detail)
        console.log(`playVideo: `, url)
        setUrl(url)
    }
    return (
        <ScrollView>
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
            {url ? null : <Image style={{width: "100%", aspectRatio: 1.5}} source={{ uri: poster}} />}
            <View style={style.tags}>
                {detail?.Genres.map((genre, index) => <Tag key={index}>{genre}</Tag>)}
            </View>
            <Text style={style.overview}>{detail?.Overview}</Text>
            <TouchableOpacity style={style.playButton} onPress={playVideo} activeOpacity={1.0}>
            <Text style={style.play}>立即播放</Text>
            </TouchableOpacity>
            <ExternalPlayer title={detail?.Name} src={getPlayUrl(detail)} />
            {seasons ? <SeasonCardList seasons={seasons} /> : null}
            <Text style={style.actorSection}>演职人员</Text>
            <ScrollView horizontal>
            {detail?.People.map((actor, index) => <ActorCard key={index} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}