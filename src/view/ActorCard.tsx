import { ThemeBasicStyle } from "@global";
import { useAppSelector } from "@hook/store";
import { People } from "@model/MediaDetail";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Image } from "./Image";
import { DEFAULT_ACTOR_AVATOR_URL } from "@helper/image";

const style = StyleSheet.create({
    root: {
        margin: 5,
    },
    image: {
        width: 60, 
        aspectRatio: 0.66,
        borderRadius: 5
    },
    name: {
        textAlign: "center",
        overflow: "hidden",
        width: 60,
        fontSize: 12,
    },
    role: {
        textAlign: "center",
        overflow: "hidden",
        fontSize: 10,
        width: 60,
    }
})

export interface ActorCardProps {
    actor: People;
    theme?: ThemeBasicStyle;
    onPress?: (actor: People) => void;
}

export function ActorCard({actor, theme, onPress}: ActorCardProps) {
    const emby = useAppSelector(state => state.emby?.emby)
    const avatorUrl = emby?.imageUrl?.(actor.Id, actor.PrimaryImageTag, "Primary")
    return (
        <TouchableOpacity activeOpacity={1.0} onPress={() => onPress?.(actor)}>
        <View style={style.root}>
            <Image style={style.image}
                resizeMode="cover"
                source={{uri: avatorUrl}}
                fallbackImages={[DEFAULT_ACTOR_AVATOR_URL]}
            />
            <Text style={{...style.name, ...theme}}
                 numberOfLines={1} 
                 ellipsizeMode="tail">
                {actor.Name}
            </Text>
            <Text style={{...style.role, ...theme}}
                numberOfLines={1} 
                ellipsizeMode="tail">
                扮演 {actor.Role}
            </Text>
        </View>
        </TouchableOpacity>
    )
}