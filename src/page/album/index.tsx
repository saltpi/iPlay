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
import React, { useEffect, useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";

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
        .catch(printException)
        .finally(() => setLoading(false))
    }, [route.params.albumId, emby])

    const rowItemWidth = (kFullScreenStyle.width -20) / Math.floor((kFullScreenStyle.width - 20) / 120)
    
    return (
            <View style={{...style.root, ...theme}}>
                {data?.Items ? <ListView items={data?.Items} 
                    style={{width: "100%", height: kFullScreenStyle.height - 50, padding: 10}}
                    layoutForType={(i, dim) => {
                        dim.width = rowItemWidth;
                        dim.height = 200;
                    }}
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