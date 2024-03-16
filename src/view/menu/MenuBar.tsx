import { NavigationProp, useNavigation } from "@react-navigation/native";
import { useState } from "react";
import { Image, StyleSheet, Text, TouchableWithoutFeedback, View } from "react-native";
const homeIcon = require("@view/menu/Home.png");
const searchIcon = require("@view/menu/Search.png");
const starIcon = require("@view/menu/Star.png");
const settingsIcon = require("@view/menu/Setting.png");

export enum MenuType {
    Home = 0,
    Search = 1,
    Star = 2,
    Settings = 3
}

const style = StyleSheet.create({
    menuBar: {
        flexDirection: "row",
        justifyContent: "space-around",
        borderTopColor: "gray",
        borderTopWidth: 0.25,
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
        aspectRatio: 1,
        // tintColor: "rgba(128, 128, 128, 0.3)",
        opacity: 0.25
    },
    activeIcon: {
        height: 25,
        aspectRatio: 1,
        tintColor: undefined
    }
});

const menu = [
    {icon: homeIcon, name: "Home"},
    {icon: searchIcon, name: "Search"},
    {icon: starIcon, name: "Star"},
    {icon: settingsIcon, name: "Settings"}
]

export function MenuBar() {
    const [active, setActive] = useState<Number>(0);
    return (
        <View style={style.menuBar}>
            {menu.map((item, i) =>
                <TouchableWithoutFeedback key={i} onPress={() => setActive(i)}>
                <View style={style.menuItem}>
                    <Image style={active === i ? style.activeIcon : style.icon} source={item.icon} />
                </View>
                </TouchableWithoutFeedback>
            )}
        </View>
    );
}