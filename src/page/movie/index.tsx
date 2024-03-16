import { imageUrl } from "@api/config";
import { Api } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { MediaDetail } from "@model/MediaDetail";
import { ActorCard } from "@view/ActorCard";
import { ExternalPlayer } from "@view/player/ExternalPlayer";
import { useEffect, useState } from "react";
import { Image, ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    overview: {
        padding: 5
    },
    actorSection: {
        marginTop: 5,
        fontWeight: "600",
        fontSize: 20,
    }
})

export function Page({route}: PropsWithNavigation<"movie">) {
    const {title, type, movie} = route.params
    const [detail, setDetail] = useState<MediaDetail>();
    useEffect(() => {
        Api.emby?.getMedia?.(Number(movie.Id)).then(data => {
            setDetail(data)
            data.People
        })
    }, [movie.Id])
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
    return (
        <ScrollView>
            <Image style={{width: "100%", aspectRatio: 1.5}} source={{ uri: imageUrl(movie.Id, movie.BackdropImageTags[0], "Backdrop/0")}} />
            <Text style={style.overview}>{detail?.Overview}</Text>
            <ExternalPlayer title={detail?.Name} src={getPlayUrl(detail)} />
            <Text style={style.actorSection}>演职人员</Text>
            <ScrollView horizontal>
            {detail?.People.map((actor, index) => <ActorCard key={index} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}