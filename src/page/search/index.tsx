import { useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { MediaCard } from "@view/MediaCard";
import { StatusBar } from "@view/StatusBar";
import { Tag } from "@view/Tag";
import { MenuBar } from "@view/menu/MenuBar";
import { useEffect, useState } from "react";
import { SafeAreaView, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

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
    const [result, setResult] = useState<Media[]>([])
    const [searchKeyword, setSearchKeyword] = useState("")
    useEffect(() => {
        emby?.searchRecommend?.()
            .then((data) => {
                console.log(data)
                setMedias(data.Items)
            })
    }, [])
    useEffect(() => {
        emby?.getItemWithName?.(searchKeyword)
            .then((data) => {
                console.log(data)
                setResult(data.Items)
            })

    }, [searchKeyword, emby])
    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <TextInput style={style.searchInput} 
                    value={searchKeyword}
                    onChangeText={setSearchKeyword}
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
                {result.map(media => <MediaCard key={media.Id} media={media} />)}
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    )
}