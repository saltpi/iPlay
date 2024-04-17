import { PropsWithNavigation } from "@global";
import { Version } from "@helper/device";
import { useAppSelector } from "@hook/store";
import { selectThemeBasicStyle } from "@store/themeSlice";
import { Image } from "@view/Image";
import { StatusBar } from "@view/StatusBar";
import { Linking, StyleSheet, Text, TouchableOpacity, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
        alignItems: "center",
        // justifyContent: "center",
    },
    logo: {
        width: 100,
        height: 100,
        marginBottom: 20,
        marginTop: "25%",
    },
    slogan: {
        fontSize: 20,
        marginBottom: 20,
    },
    version: {
        marginBottom: 10,
        fontWeight: "bold",
    },
    build: {
        fontStyle: "italic",
        marginBottom: 10,
    },
    link: {
        marginTop: 10,
        marginBottom: 20,
        fontSize: 16,
        color: "blue",
    },
    gift: {
        paddingBottom: "5%"
    }
});


export function Page(props: PropsWithNavigation<"default">) {
    const theme = useAppSelector(selectThemeBasicStyle)
    const url = "https://github.com/ourfor/iPlayClient"
    const pagePaddingTop = useAppSelector(state => state.theme.pagePaddingTop)
    return (
        <View style={{...style.page, ...theme, paddingTop: pagePaddingTop}}>
            <StatusBar />
            <Image style={style.logo} source={require("@asset/logo.png")} />
            <Text style={{...style.slogan, ...theme}}>
                一个跨平台的视频播放器
            </Text>
            <Text style={{...style.version, ...theme}}>
                Version: {Version.versionCode}
            </Text>
            <Text style={{...style.build, ...theme}}>
                Build: {Version.buildNumber}
            </Text>
            <TouchableOpacity activeOpacity={1}
                onPress={() => Linking.openURL(url)}>
                <Text style={{...style.link, ...theme}}>{url}</Text>
            </TouchableOpacity>
            <Text>
                🥳 觉得好用就给个🌟吧
            </Text>
            <View style={style.gift}>

            </View>
        </View>
    )
}