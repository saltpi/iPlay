import { Emby } from "@api/emby";
import { PropsWithNavigation } from "@global";
import { printException } from "@helper/log";
import { useAppSelector } from "@hook/store";
import { EmbyResponse } from "@model/EmbyResponse";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { ListView, kFullScreenStyle } from "@view/ListView";
import { MediaCard } from '@view/MediaCard';
import { Spin, SpinBox } from "@view/Spin";
import _ from "lodash";
import React, { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "center",
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
    const [data, setData] = useState<Media[]>()
    const [totalCount, setTotalCount] = useState(0)
    const [loading, setLoading] = useState(true)
    const [page, setPage] = useState(1)
    const theme = useAppSelector(selectThemeBasicStyle)
    useEffect(() => {
        if (!emby) return
        setLoading(true)
        getAlbum(emby, Number(route.params.albumId), page)
        .then(res => {
            const total = res.data?.TotalRecordCount
            const newItems = res.data?.Items ?? []
            setTotalCount(total ?? 0)
            setData(data => [...(data ?? []), ...newItems]),
            setPage(page => page + 1)
        })
        .catch(printException)
        .finally(() => setLoading(false))
    }, [route.params.albumId, emby])

    const onEndReached = () => {
        if (!emby) return
        if (data?.length === totalCount) return
        setLoading(true)
        getAlbum(emby, Number(route.params.albumId), page)
        .then(res => {
            const newItems = res.data?.Items ?? []
            setData(data => [...(data ?? []), ...newItems]),
            setPage(page => page + 1)
        })
        .catch(printException)
        .finally(() => setLoading(false))
    }

    const rowItemWidth = (kFullScreenStyle.width -20) / Math.floor((kFullScreenStyle.width - 20) / 120)
    
    return (
            <View style={{...style.root, ...theme}}>
                {data ? <ListView items={data} 
                    style={{width: "100%", flex: 1, padding: 10}}
                    layoutForType={(i, dim) => {
                        dim.width = rowItemWidth;
                        dim.height = 200;
                    }}
                    onEndReached={onEndReached}
                    render={media =>
                        <MediaCard key={media.Id}
                            media={media} 
                            theme={theme} />
                    }
                /> : null}
                {loading ? <Spin color={theme.color} /> : null}
            </View>
    )
}