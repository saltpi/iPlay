import { Emby } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { EmbyResponse } from "@model/EmbyResponse";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { MediaCard } from '@view/MediaCard';
import { Spin } from "@view/Spin";
import React, { useEffect, useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-around",
        minHeight: 100,
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
    const [loading, setLoading] = useState(true)
    const theme = useAppSelector(selectThemeBasicStyle)
    useEffect(() => {
        if (!emby) return
        setLoading(true)
        getAlbum(emby, Number(route.params.albumId))
        .then(res => {
            setData(res.data)
        })
        .finally(() => setLoading(false))
    }, [route.params.albumId, emby])
    return (
        <ScrollView showsHorizontalScrollIndicator={false}>
            <View style={{...style.root, ...theme}}>
                {data?.Items.map(media => <MediaCard key={media.Id} media={media} theme={theme} />)}
                {loading ? <Spin color={theme.color} /> : null}
            </View>
        </ScrollView>
    )
}