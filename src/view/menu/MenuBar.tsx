import { NavigationProp, useNavigation } from "@react-navigation/native";
import { Image, StyleSheet, Text, View } from "react-native";
const homeIcon = require("@view/menu/Home.png");
const searchIcon = require("@view/menu/Search.png");
const starIcon = require("@view/menu/Star.png");
const settingsIcon = require("@view/menu/Setting.png");

const style = StyleSheet.create({
    menuBar: {
        flexDirection: "row",
        justifyContent: "space-around",
        borderTopColor: "gray",
        borderTopWidth: 0.5,
        paddingTop: 8,
        paddingBottom: 2.5,
        alignItems: "center",
        flexShrink: 0,
        flexGrow: 0,
        flexBasis: "auto"
    },
    menuItem: {
        flex: 1,
        alignItems: "center",
        textAlign: "center"
    },
    icon: {
        height: 25,
        aspectRatio: 1
    }
});

export function MenuBar() {
    const navigation = useNavigation<NavigationProp<any>>();
    return (
        <View style={style.menuBar}>
            <View style={style.menuItem}>
                <Image style={style.icon} source={homeIcon} />
            </View>
            <View style={style.menuItem}>
                <Image style={style.icon} source={searchIcon} />
            </View>
            <View style={style.menuItem}>
                <Image style={style.icon} source={starIcon} />
            </View>
            <View style={style.menuItem}>
                <Image style={style.icon} source={settingsIcon} />
            </View>
        </View>
    );
}