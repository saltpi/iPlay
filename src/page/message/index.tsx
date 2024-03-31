import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { Episode } from "@model/Episode";
import { Media } from "@model/Media";
import { selectThemeBasicStyle } from "@store/themeSlice";
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
        paddingLeft: 8,
        paddingRight: 8,
    },
    sectionTitle: {
        fontSize: 22,
    }
});

export function Page({navigation}: PropsWithNavigation<"default">) {
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const emby = useAppSelector(state => state.emby.emby)
    const [items, setItems] = useState<Media[]>([])
    const menuBarHeight = useAppSelector(state => state.theme.menuBarHeight)
    const theme = useAppSelector(selectThemeBasicStyle)
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
        <View style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                style={{flex: 1, marginBottom: menuBarHeight}}>
            <View style={style.watchList}>
                {items.length !== 0 ? <Text style={{...style.sectionTitle, ...theme}}>观看记录</Text> : null}
                {items.map((item, idx) => (
                    item.Type === "Movie" ?
                    <MediaCard key={idx} media={item} 
                        theme={theme}
                        /> :
                    <EpisodeCard key={idx} emby={emby}
                        onPress={() => onPress(item)} 
                        episode={item as any} />
                ))}
            </View>
            </ScrollView>
        </View>
    )
}