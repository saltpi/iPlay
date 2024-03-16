import { imageUrl } from "@api/config";
import { People } from "@model/MediaDetail";
import { Image, StyleSheet, Text, View } from "react-native";

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
    },
    role: {
        textAlign: "center",
        overflow: "hidden",
        width: 120,
    }
})

export function ActorCard({actor}: {actor: People}) {
    return (
        <View style={style.root}>
            <Image style={style.image} source={{uri: imageUrl(actor.Id, actor.PrimaryImageTag, "Primary")}} />
            <Text style={style.name}>{actor.Name}</Text>
            <Text style={style.role}
                numberOfLines={1} 
                ellipsizeMode="tail">
                扮演 {actor.Role}
            </Text>
        </View>
    )
}