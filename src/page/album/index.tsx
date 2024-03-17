import { Api, Emby } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { EmbyResponse } from "@model/EmbyResponse";
import { Media } from "@model/Media";
import { MediaCard } from '@view/MediaCard';
import React, { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-around",
    }
})

async function getAlbum(emby: Emby, id: number, page: number = 1) {
    const album = await emby?.getMedia?.(id)
    const type = album?.CollectionType === "tvshows" ? "Series" : "Movie"
    const data = await emby?.getCollection?.(id, type, page)
    return {album, data}
}

export function Page({route, navigation}: PropsWithNavigation<"album">) {
    const emby = useAppSelector(state => state.emby?.emby)
    const [data, setData] = useState<EmbyResponse<Media>>()
    useEffect(() => {
        if (!emby) return
        getAlbum(emby, Number(route.params.albumId))
        .then(res => {
            setData(res.data)
        })
    }, [route.params.albumId, emby])
    return (
        <ScrollView showsHorizontalScrollIndicator={false}>
            <View style={style.root}>
                {data?.Items.map(media => <MediaCard key={media.Id} media={media} />)}
            </View>
        </ScrollView>
    )
}