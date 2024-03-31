import { PropsWithNavigation } from "@global";
import { printException } from "@helper/log";
import { useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { MediaCard } from "@view/MediaCard";
import { Spin } from "@view/Spin";
import { StatusBar } from "@view/StatusBar";
import { useEffect, useState } from "react";
import { RefreshControl, SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    sectionTitle: {
        fontSize: 18,
        fontWeight: "bold",
        marginTop: 20,
        marginBottom: 10,
        paddingLeft: 20,
    },
    movieList: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignContent: "flex-start",
        justifyContent: "space-between",
        paddingLeft: 10,
        paddingRight: 10,
    },
    episodeList: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignContent: "flex-start",
        justifyContent: "space-between",
        paddingLeft: 10,
        paddingRight: 10,
    },
});


export function Page(props: PropsWithNavigation<"default">) {
    const theme = useAppSelector(selectThemeBasicStyle)
    const emby = useAppSelector(state => state.emby.emby)
    const [loading, setLoading] = useState(false)
    const [favoriteMovies, setFavoriteMovies] = useState<Media[]>([])
    const [favoriteEpisodes, setFavoriteEpisodes] = useState<Media[]>([])
    const menuBarHeight = useAppSelector(state => state.theme.menuBarHeight)
    const fetchFavoriteItems = () => {
        Promise.all(
            ["Movie", "Episode"].map((type: any) => emby?.getItem?.({type, Filters: "IsFavorite"}))
        ).then(result => {
            result.forEach((res, idx) => {
                if (idx === 0) setFavoriteMovies(res?.Items ?? [])
                else if (idx === 1) setFavoriteEpisodes(res?.Items ?? [])
            })
        })
        .catch(printException)
        .finally(() => { 
            setLoading(false)
            setRefreshing(false)
        })
    }

    const pagePaddingTop = useAppSelector(state => state.theme.pagePaddingTop)
    const [refreshing, setRefreshing] = useState(false)
    const onRefresh = () => {
        setRefreshing(true)
        fetchFavoriteItems()
    }

    useEffect(() => {
        setLoading(true)
        fetchFavoriteItems()
    }, [emby])

    return (
        <View style={{...style.page, ...theme, paddingTop: pagePaddingTop}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                style={{flex: 1, marginBottom: menuBarHeight}}>
                {loading ? 
                <Spin color={theme.color} /> 
                : null}
                <View>
                    {favoriteMovies.length > 0 ? 
                    <Text style={{...style.sectionTitle, ...theme}}>喜爱的电影</Text>
                    : null}
                    <View style={style.movieList}>
                    {favoriteMovies.map((movie, idx) => 
                        <MediaCard key={idx} media={movie} theme={theme} />)}
                    </View>
                    {favoriteEpisodes.length > 0 ?
                    <Text style={{...style.sectionTitle, ...theme}}>喜爱的剧集</Text>
                    : null}
                    <View style={style.episodeList}>
                    {favoriteEpisodes.map((episode, idx) => 
                        <MediaCard key={idx} media={episode} theme={theme} />)}
                    </View>
                </View>
            </ScrollView>
        </View>
    )
}