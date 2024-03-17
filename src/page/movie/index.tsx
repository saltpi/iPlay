import { Api } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { MediaDetail } from "@model/MediaDetail";
import { Season } from "@model/Season";
import { ActorCard } from "@view/ActorCard";
import { SeasonCardList } from "@view/SeasonCard";
import { Tag } from "@view/Tag";
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
    },
    tags: {
        flexDirection: "row",
        marginTop: 5,
        paddingLeft: 2.5,
        paddingRight: 2.5,
    }
})

export function Page({route}: PropsWithNavigation<"movie">) {
    const {title, type, movie} = route.params
    const [detail, setDetail] = useState<MediaDetail>();
    const [seasons, setSeasons] = useState<Season[]>();
    useEffect(() => {
        Api.emby?.getMedia?.(Number(movie.Id)).then(setDetail)
        if (type !== "Series") return
        Api.emby?.getSeasons?.(Number(movie.Id)).then(setSeasons)
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
            <Image style={{width: "100%", aspectRatio: 1.5}} source={{ uri: Api.emby?.imageUrl?.(movie.Id, movie.BackdropImageTags[0], "Backdrop/0")}} />
            <View style={style.tags}>
                {detail?.Genres.map((genre, index) => <Tag key={index}>{genre}</Tag>)}
            </View>
            <Text style={style.overview}>{detail?.Overview}</Text>
            <ExternalPlayer title={detail?.Name} src={getPlayUrl(detail)} />
            {seasons ? <SeasonCardList seasons={seasons} /> : null}
            <Text style={style.actorSection}>演职人员</Text>
            <ScrollView horizontal>
            {detail?.People.map((actor, index) => <ActorCard key={index} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}