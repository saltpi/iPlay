import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { useEffect, useMemo, useState } from "react";
import { ScrollView, StyleSheet, TextStyle, View, ViewStyle } from "react-native";
import { Season } from "@model/Season";
import { Episode } from "@model/Episode";
import { EpisodeCard } from "@view/EpisodeCard";
import { Image } from '@view/Image';
import { StatusBar } from "@view/StatusBar";
import { logger, printException } from "@helper/log";
import { selectThemeBasicStyle, selectThemedPageStyle } from "@store/themeSlice";
import { Device } from "@helper/device";
import { Text } from "react-native";

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
    const isTablet = Device.isTablet
    const layout = useMemo(() => ({
        page: {
            ...style.page, backgroundColor,
            flexDirection: isTablet ? "row" : "column"
        } as ViewStyle,
        cover: {
            ...style.cover,
            minHeight: pageStyle.paddingTop,
            aspectRatio: season.PrimaryImageAspectRatio,
        },
        season: {
            width: isTablet ? "40%" : "100%",
            flexDirection: "column",
        } as ViewStyle,
        overview: {
            ...theme,
            textAlign: "center",
            marginTop: 10,
            fontSize: 18,
            fontWeight: "bold",
        } as TextStyle
    }), [pageStyle, season])

    logger.info(`season`, season)
    return (
        <View style={layout.page}>
            <StatusBar />
            {isTablet ?
            <View style={layout.season}>
                <Image style={layout.cover} 
                    source={{uri: coverUrl}} />
                <Text style={layout.overview}>{season.Name}</Text>
            </View>
            : null}
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, backgroundColor}}>
                {isTablet ? null :
                <View>
                    <Image style={layout.cover} 
                        source={{uri: coverUrl}} />
                </View>
                }
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