import { imageUrl } from "@api/config";
import { Api } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { MediaDetail } from "@model/MediaDetail";
import { ActorCard } from "@view/ActorCard";
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
    return (
        <ScrollView>
            <Image style={{width: "100%", aspectRatio: 1.5}} source={{ uri: imageUrl(movie.Id, movie.BackdropImageTags[0], "Backdrop/0")}} />
            <Text style={style.overview}>{detail?.Overview}</Text>
            <Text style={style.actorSection}>演职人员</Text>
            <ScrollView horizontal>
            {detail?.People.map((actor, index) => <ActorCard key={index} actor={actor} />)}
            </ScrollView>
        </ScrollView>
    )
}