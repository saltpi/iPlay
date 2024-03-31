import { PropsWithNavigation } from "@global";
import { printException } from "@helper/log";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { fetchEmbyActorAsync, fetchEmbyActorWorksAsync } from "@store/embySlice";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { Image } from "@view/Image";
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

    console.log(actorData)
    useEffect(() => {
        const aid = id || actor?.Id
        if (!aid) return
        console.log("fetch actor: ", aid)
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


    return (
        <View style={{...style.page, paddingTop: pageStyle.paddingTop}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, ...theme}}>
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
                <Text style={style.worksSection}>相关作品</Text>
                <View style={style.works}>
                    {media.map(item => <MediaCard key={item.Id} media={item} theme={theme} />)}
                </View>
            </ScrollView>
        </View>
    )
}