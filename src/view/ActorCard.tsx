import { Api } from "@api/emby";
import { ThemeBasicStyle } from "@global";
import { useAppSelector } from "@hook/store";
import { People } from "@model/MediaDetail";
import { Image, StyleSheet, Text, View, ViewStyle } from "react-native";

const style = StyleSheet.create({
    root: {
        margin: 5,
    },
    image: {
        width: 120, 
        aspectRatio: 0.66,
        borderRadius: 5
    },
    name: {
        textAlign: "center",
        overflow: "hidden",
        width: 120,
    },
    role: {
        textAlign: "center",
        overflow: "hidden",
        width: 120,
    }
})

export function ActorCard({actor, theme}: {actor: People, theme?: ThemeBasicStyle}) {
    const emby = useAppSelector(state => state.emby?.emby)
    return (
        <View style={style.root}>
            <Image style={style.image} source={{uri: emby?.imageUrl?.(actor.Id, actor.PrimaryImageTag, "Primary")}} />
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
    )
}