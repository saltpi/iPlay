import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { useEffect, useState } from "react";
import { ScrollView, StyleSheet, View } from "react-native";
import { Season } from "@model/Season";
import { Episode } from "@model/Episode";
import { EpisodeCard } from "@view/EpisodeCard";
import { Image } from '@view/Image';
import { StatusBar } from "@view/StatusBar";
import { printException } from "@helper/log";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    cover: {
        width: "100%",
        aspectRatio: 16/9,
    }
});

export type SeasonPageProps = PropsWithNavigation<'season'>;
export function Page({route, navigation}: SeasonPageProps) {
    const season: Season = route.params.season
    const emby = useAppSelector(state => state.emby?.emby)
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const theme = useAppSelector(selectThemeBasicStyle)
    const [episodes, setEpisodes] = useState<Episode[]>([])
    useEffect(() => {
        emby?.getEpisodes?.(Number(season.SeriesId), Number(season.Id))
        .then(setEpisodes)
        .catch(printException)
    }, [emby, season])

    const onPress = async (episode: Episode) => {
        navigation.navigate('player', {
            title: episode.Name,
            episode,
            episodes,
        });
    }

    const coverUrl = emby?.imageUrl?.(season.Id, season.BackdropImageTags[0])
    const coverStyle = {
        ...style.cover,
        minHeight: pageStyle.paddingTop,
        aspectRatio: season.PrimaryImageAspectRatio
    }
    return (
        <View style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, backgroundColor}}>
                <View>
                    <Image style={coverStyle} 
                        source={{uri: coverUrl}} />
                </View>
                {episodes?.map(episode => 
                    <EpisodeCard key={episode.Id} emby={emby}
                        onPress={onPress}
                        theme={theme}
                        episode={episode} />
                )}
            </ScrollView>
        </View>
    )
}