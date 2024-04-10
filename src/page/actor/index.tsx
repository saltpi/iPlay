import { PropsWithNavigation } from "@global";
import { windowHeight } from "@helper/device";
import { printException } from "@helper/log";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { fetchEmbyActorAsync, fetchEmbyActorWorksAsync } from "@store/embySlice";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { Image } from "@view/Image";
import { ListView, kFullScreenStyle } from "@view/ListView";
import { MediaCard } from "@view/MediaCard";
import { Spin } from "@view/Spin";
import { StatusBar } from "@view/StatusBar";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    info: {
        width: "100%",
        flexDirection: "row",
    },
    overview: {
        width: "60%",
        margin: 5,
        fontSize: 14,
    },
    avator: {
        width: "35%",
        aspectRatio: 0.66,
        flexShrink: 1,
        flexGrow: 0,
        borderRadius: 5,
        margin: 5,
    },
    worksSection: {
        fontSize: 16,
        fontWeight: "600",
        margin: 5,
    },
    works: {
        width: "100%",
        flexWrap: "wrap",
        flexDirection: "row",
        alignItems: "flex-start",
    }
});


export function Page({route: {params: {id, actor}}}: PropsWithNavigation<"actor">) {
    const theme = useAppSelector(selectThemeBasicStyle)
    const [loading, setLoading] = useState(false)
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const actorData = useAppSelector(state => state.emby.source?.actors?.[id ?? actor?.Id ?? ""])
    const [media, setMedia] = useState<Media[]>([])
    const dispatch = useAppDispatch()

    useEffect(() => {
        const aid = id || actor?.Id
        if (!aid) return
        setLoading(true)
        dispatch(fetchEmbyActorAsync(aid))
            .then(() => setLoading(false))
            .catch(printException)
        dispatch(fetchEmbyActorWorksAsync(aid))
            .then((res) => {
                const data = res.payload
                if (data && typeof data !== "string") setMedia(data)
            })
            .catch(printException)
    }, [id, actor])

    const rowItemWidth = (kFullScreenStyle.width - 20) / Math.floor((kFullScreenStyle.width - 20) / 120);

    return (
        <View style={{...style.page, paddingTop: pageStyle.paddingTop}}>
            <StatusBar />
            <View style={style.info}>
                <Image style={{...style.avator}} 
                    source={{uri: actorData?.avatar}} />
                <Text style={{...style.overview, ...theme}}
                    numberOfLines={13} 
                    ellipsizeMode="tail">
                    {actorData?.overview}
                </Text>
            </View>
            {loading ? <Spin /> : null}
            {media?.length >= 0 ?
            <Text style={{...style.worksSection, ...theme}}>相关作品</Text>
            : null}
            {media?.length  > 0 ?
            <ListView items={media}
                style={{width: '100%', flex: 1, padding: 10}}
                render={item => <MediaCard media={item} theme={theme} />}
                layoutForType={(item, dim) => {
                    dim.width = rowItemWidth
                    dim.height = 200
                }}
            />
            : null }
        </View>
    )
}