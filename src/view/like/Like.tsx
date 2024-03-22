import { Media } from "@model/Media";
import { StyleSheet, View } from "react-native";
import { Text } from "react-native-svg";
import FavoriteIcon from "@asset/favorite.svg"

const style = StyleSheet.create({
    root: {
        padding: 2.5,
        alignItems: "center"
    },
    icon: {
        height: 28,
    }
});

export interface LikeProps {
    media: Media;
}

export function Like({media}: LikeProps) {
    return (
        <View style={style.root}>
            <FavoriteIcon width={style.icon.height}
                style={style.icon} />
        </View>
    )
}