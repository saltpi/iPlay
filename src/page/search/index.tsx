import { printException } from "@helper/log";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { fetchRecommendationsAsync, searchMediaAsync } from "@store/embySlice";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { MediaCard } from "@view/MediaCard";
import { Spin } from "@view/Spin";
import { StatusBar } from "@view/StatusBar";
import { Tag } from "@view/Tag";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, TextInput, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    recommendations: {
        flexDirection: "row",
        flexWrap: "wrap",
        padding: 10,
        alignContent: "center",
        justifyContent: "center",
        marginTop: 10,
    },
    searchInput: {
        margin: 10,
        padding: 5,
        textAlign: "center",
        backgroundColor: "#f0f0f0",
        borderColor: "#e0e0e0",
        borderWidth: 1,
        borderRadius: 5,
    },
    result: {
        flexDirection: "row",
        flexWrap: "wrap",
        padding: 10,
        alignContent: "center",
        justifyContent: "center",
    }
});

export const colors = [
    "cyan", "gold", "magenta", "orange", "lime",
    "green", "blue", "purple", "red", "volcano",
    "pink", "geekblue", "cyan", "gold", "magenta",
]

export function Page() {
    const [loading, setLoading] = useState(false)
    const recommendations = useAppSelector(state => state.emby.source.recommendations)
    const [result, setResult] = useState<Media[]>([])
    const [searchKeyword, setSearchKeyword] = useState("")
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const site = useAppSelector(state => state.emby.site)
    const dispatch = useAppDispatch()
    useEffect(() => {
        setLoading(true)
        dispatch(fetchRecommendationsAsync())
        .catch(printException)
        .finally(() => setLoading(false))
    }, [site])

    useEffect(() => {
        const keyword = searchKeyword.trim()
        if (keyword.length === 0) {
            return
        }
        dispatch(searchMediaAsync(keyword))
            .then(res => {
                if (typeof res.payload == "string") return
                setResult(res.payload ?? [])
            })
            .catch(printException)
    }, [searchKeyword, site])
    
    return (
        <View style={{...style.page, ...pageStyle}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, backgroundColor}}>
                <TextInput style={{...style.searchInput, ...theme}} 
                    value={searchKeyword}
                    onChangeText={setSearchKeyword}
                    placeholderTextColor={theme.color}
                    placeholder="搜索" />
                <View style={style.recommendations}>
                    {recommendations?.map((media, i) => 
                        <Tag key={media.Id} onPress={() => setSearchKeyword(media.Name)} 
                            color={colors[i%colors.length] as any}>
                            {media.Name}
                        </Tag>
                    )}
                </View>
                <View style={style.result}>
                {result.map(media => <MediaCard key={media.Id} media={media} theme={theme} />)}
                </View>
            </ScrollView>
            {loading ? <Spin color={theme.color} /> : null}
        </View>
    )
}