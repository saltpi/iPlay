import { Image, StyleSheet, Text, TouchableHighlight, View } from "react-native";

const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        padding: 10,
        paddingLeft: 20,
        paddingRight: 20,
        alignItems: "center",
        backgroundColor: "white",
        borderTopColor: "lightgray",
        borderTopWidth: 0.25,
    },
    label: {
        fontWeight: "300",
    },
    icon: {
        width: 24,
        aspectRatio: 1,
        marginRight: 10,
        objectFit: "contain"
    },
    indicator: {
        position: "absolute",
        right: 20,
        objectFit: "contain",
        aspectRatio: 1,
        width: 20,
    }
});

export const Icon = {
    Video: require("@view/settings/video.png"),
    Audio: require("@view/settings/earphone.png"),
    Trash: require("@view/settings/trash.png"),
    Message: require("@view/settings/message.png"),
    Indicator: require("@view/settings/right-arrows.png"),
    Mobile: require("@view/settings/mobile.png"),
}

export type IconType = keyof typeof Icon;

export interface SettingItemProps {
    icon: IconType;
    label: string;
    onPress: () => void;
}

export function SettingItem(props: SettingItemProps) {
    const icon = Icon[props.icon];
    return (
        <TouchableHighlight onPress={props.onPress} underlayColor={"lightgray"}>
        <View style={style.root}>
            {icon ? <Image style={style.icon} source={icon} /> : null}
            <Text style={style.label}>{props.label}</Text>
            <Image style={style.indicator} source={Icon.Indicator} />
        </View>
        </TouchableHighlight>
    )
}