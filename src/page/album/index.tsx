import { Api } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { EmbyResponse } from "@model/EmbyResponse";
import { Media } from "@model/Media";
import { MediaCard } from "@view/Album";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-around",
    }
})

async function getAlbum(id: number, page: number = 1) {
    const album = await Api.emby?.getMedia?.(id)
    const type = album?.CollectionType === "tvshows" ? "Series" : "Movie"
    const data = await Api.emby?.getCollection?.(id, type, page)
    return {album, data}
}

export function Page({route, navigation}: PropsWithNavigation<"album">) {
    const [data, setData] = useState<EmbyResponse<Media>>()
    useEffect(() => {
        console.log(route.params.albumId)
        getAlbum(Number(route.params.albumId))
        .then(res => {
            setData(res.data)
        })
    }, [route.params.albumId])
    return (
        <ScrollView>
            <View style={style.root}>
                {data?.Items.map(media => <MediaCard key={media.Id} media={media} />)}
            </View>
        </ScrollView>
    )
}