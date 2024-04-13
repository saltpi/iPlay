import { Emby } from "@api/emby";
import { Episode } from "@model/Episode";
import { StyleSheet, Text, TouchableOpacity, View, ViewStyle } from "react-native";
import { Image } from '@view/Image';
import { useAppSelector } from "@hook/store";
import { Tag } from "./Tag";
import { Like } from "./like/Like";
import { PlayCount } from "./counter/PlayCount";
import { ThemeBasicStyle } from "@global";
import { getImageUrl } from "@store/embySlice";

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
        maxHeight: 128
    },
    No: {
        overflow: "hidden",
        position: "absolute",
        right: 10,
        bottom: 10,
    },
    icon: {
        width: 24,
    },
    actionBar: {
        flexDirection: "row",
        flexWrap: "wrap",
        alignItems: "center",
    }
});

export interface EpisodeCardProps {
    style?: Partial<ViewStyle>
    episode: Episode;
    onPress?: (episode: Episode) => void;
    theme?: ThemeBasicStyle
}

export function EpisodeCard({style: extraStyle, theme, episode, onPress}: EpisodeCardProps) {
    const thumbUrl = useAppSelector(getImageUrl(episode.Id, episode.ImageTags.Primary))
    const posterUrl = useAppSelector(getImageUrl(episode.SeasonId, episode.ImageTags.Primary))
    return (
        <TouchableOpacity activeOpacity={1.0} onPress={() => onPress?.(episode)}>
        <View style={{...style.basic, ...theme, ...extraStyle}}>
            <Image style={{...style.cover, aspectRatio: 16/9}}
                fallbackImages={[posterUrl ?? ""]}
                source={{uri: thumbUrl}} />
            <View style={{...style.text, ...theme}}>
                <Text style={{...style.title, ...theme}}>{episode.Name}</Text>
                <Text style={{...style.overview, ...theme}}
                    numberOfLines={6} 
                    ellipsizeMode="tail">
                    {episode.Overview ?? DEFULT_OVERVIEW}
                </Text>
                <View style={style.actionBar}>
                <Like id={Number(episode.Id ?? 0)}
                    width={style.icon.width}
                    isFavorite={episode.UserData.IsFavorite}
                    />
                <PlayCount 
                    width={style.icon.width + 6}
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