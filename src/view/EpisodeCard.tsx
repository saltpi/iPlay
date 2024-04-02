import { Emby } from "@api/emby";
import { Episode } from "@model/Episode";
import { StyleSheet, Text, TouchableOpacity, View, ViewStyle } from "react-native";
import { Image } from '@view/Image';
import { useAppSelector } from "@hook/store";
import { Tag } from "./Tag";
import { Like } from "./like/Like";
import { PlayCount } from "./counter/PlayCount";
import { ThemeBasicStyle } from "@global";

const DEFULT_OVERVIEW = `数据源中缺少相关描述
Data source lacks relevant description`

const style = StyleSheet.create({
    root: {

    },
    basic: {
        flexDirection: "row",
        paddingTop: 10,
        paddingBottom: 10,
    },
    cover: {
        width: "35%",
        marginLeft: 10,
        borderRadius: 5,
        flexShrink: 0,
        flexGrow: 0,
    },
    text: {
        flex: 1,
        textAlign: "center",
        paddingLeft: 5,
        paddingRight: 2.5
    },
    title: {
        fontWeight: "600"
    },
    overview: {
        color: "gray",
        maxHeight: 100
    },
    No: {
        overflow: "hidden",
        position: "absolute",
        right: 10,
        bottom: 10,
    },
    icon: {
        width: 32,
    },
    favorite: {
        width: 32,
        height: 32,
        flexShrink: 0,
        flexGrow: 0,
    },
    actionBar: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
    }
});

export interface EpisodeCardProps {
    style?: Partial<ViewStyle>
    emby?: Emby|null;
    episode: Episode;
    onPress?: (episode: Episode) => void;
    theme?: ThemeBasicStyle
}

export function EpisodeCard({style: extraStyle, theme, emby, episode, onPress}: EpisodeCardProps) {
    const thumbUrl = emby?.imageUrl?.(episode.Id, episode.ImageTags.Primary)
    const posterUrl = emby?.imageUrl?.(episode.SeasonId, episode.ImageTags.Primary)
    return (
        <TouchableOpacity activeOpacity={1.0} onPress={() => onPress?.(episode)}>
        <View style={{...style.basic, ...theme, ...extraStyle}}>
            <Image style={{...style.cover, aspectRatio: episode.PrimaryImageAspectRatio}}
                fallbackImages={[posterUrl ?? ""]}
                source={{uri: thumbUrl}} />
            <View style={{...style.text, ...theme}}>
                <Text style={{...style.title, ...theme}}>{episode.Name}</Text>
                <Text style={{...style.overview, ...theme}}
                    numberOfLines={10} 
                    ellipsizeMode="tail"
                    >
                    {episode.Overview ?? DEFULT_OVERVIEW}
                </Text>
                <View style={style.actionBar}>
                <Like id={Number(episode.Id ?? 0)}
                    emby={emby}
                    isFavorite={episode.UserData.IsFavorite}
                    />
                <PlayCount 
                    style={theme}
                    count={episode?.UserData?.PlayCount ?? 0} />
                </View>
            </View>
            <Tag style={style.No} color="green">
                {episode.IndexNumber}
            </Tag>
        </View>
        </TouchableOpacity>
    )
}