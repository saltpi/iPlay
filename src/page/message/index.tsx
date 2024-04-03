import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { Media } from "@model/Media";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { EpisodeCard } from "@view/EpisodeCard";
import { MediaCard } from "@view/MediaCard";
import { useEffect, useState } from "react";
import { RefreshControl, SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    loading: {

    },
    watchList: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-around",
        paddingLeft: 8,
        paddingRight: 8,
        color: "green"
    },
    sectionTitle: {
        fontSize: 22,
        marginLeft: 8,
    }
});

export function Page({navigation}: PropsWithNavigation<"default">) {
    const emby = useAppSelector(state => state.emby.emby)
    const [items, setItems] = useState<Media[]>([])
    const theme = useAppSelector(selectThemeBasicStyle)
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const [refreshing, setRefreshing] = useState(false)
    const onRefresh = () => {
        setRefreshing(true)
        emby?.getResume?.().then(res => {
            setItems(res ?? [])
            setRefreshing(false)
        })
    }
    const onPress = (media: Media) => {
        navigation.navigate('movie', {
            title: media.Name,
            type: media.Type,
            movie: media,
        });
    };
    useEffect(() => {
        emby?.getResume?.().then(res => {
            setItems(res ?? [])
        })
    }, [emby])
    return (
        <View style={{...style.page, ...pageStyle}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                refreshControl={<RefreshControl refreshing={refreshing} 
                onRefresh={onRefresh} />}
                style={{flex: 1}}>
            {items.length !== 0 ? <Text style={{...style.sectionTitle, ...theme}}>观看记录</Text> : null}
            <View style={style.watchList}>
                {items.map((item, idx) => (
                    item.Type === "Movie" ?
                    <MediaCard key={idx} 
                        media={item} 
                        theme={theme}
                        /> :
                    <EpisodeCard key={idx} emby={emby}
                        style={{minWidth: "100%", maxWidth: "100%"}}
                        theme={theme}
                        onPress={() => onPress(item)} 
                        episode={item as any} />
                ))}
            </View>
            </ScrollView>
        </View>
    )
}