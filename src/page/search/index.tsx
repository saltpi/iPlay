import { printException } from "@helper/log";
import { useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { MediaCard } from "@view/MediaCard";
import { Spin } from "@view/Spin";
import { StatusBar } from "@view/StatusBar";
import { Tag } from "@view/Tag";
import { useEffect, useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, TextInput, View } from "react-native";

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
    const emby = useAppSelector(state => state.emby.emby)
    const [medias, setMedias] = useState<Media[]>([])
    const [loading, setLoading] = useState(false)
    const [result, setResult] = useState<Media[]>([])
    const [searchKeyword, setSearchKeyword] = useState("")
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)
    useEffect(() => {
        setLoading(true)
        emby?.searchRecommend?.()
            .then((data) => {
                setMedias(data.Items)
            })
            .catch(printException)
            .finally(() => setLoading(false))
    }, [])
    useEffect(() => {
        const keyword = searchKeyword.trim()
        if (keyword.length === 0) {
            return
        }
        emby?.getItemWithName?.(keyword)
            .then((data) => {
                setResult(data.Items)
            })
            .catch(printException)
    }, [searchKeyword, emby])
    return (
        <View style={{...style.page, ...theme}}>
            <StatusBar backgroundColor={"transparent"} translucent />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, backgroundColor}}>
                <TextInput style={{...style.searchInput, ...theme}} 
                    value={searchKeyword}
                    onChangeText={setSearchKeyword}
                    placeholderTextColor={theme.color}
                    placeholder="搜索" />
                <View style={style.recommendations}>
                    {medias.map((media, i) => 
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