import { PropsWithNavigation } from "@global";
import { printException } from "@helper/log";
import { useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { MediaCard } from "@view/MediaCard";
import { Spin, SpinBox } from "@view/Spin";
import { StatusBar } from "@view/StatusBar";
import { useEffect, useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";

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
    const fetchFavoriteItems = () => {
        setLoading(true)
        Promise.all(
            ["Movie", "Episode"].map((type: any) => emby?.getItem?.({type, Filters: "IsFavorite"}))
        ).then(result => {
            result.forEach((res, idx) => {
                if (idx === 0) setFavoriteMovies(res?.Items ?? [])
                else if (idx === 1) setFavoriteEpisodes(res?.Items ?? [])
            })
        })
        .catch(printException)
        .finally(() => setLoading(false))
    }

    useEffect(() => {
        fetchFavoriteItems()
    }, [emby])

    return (
        <SafeAreaView style={{...style.page, ...theme}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
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
        </SafeAreaView>
    )
}