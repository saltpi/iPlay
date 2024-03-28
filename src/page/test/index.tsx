import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { iOSMPVPlayer, Video } from "@view/Video";
import { useEffect, useRef } from "react";
import { useAppSelector } from "@hook/store";
import { MPVPlayer, AndroidMPVPlayerView, DemoView } from "@view/mpv/AndroidMPVPlayer";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    video: {
        width: "100%",
        aspectRatio: 16/9,
    }
});

export function Page() {
    const ref= useRef<any>(null);
    const videoRef = useRef<any>(null);
    const barStyle = useAppSelector(state => state.theme.barStyle);
    const url = "https://drive.endemy.me/iplay/hexo1.mp4"
    useEffect(() => {
        return () => {
            console.log(`unmount`, ref.current)
            console.log(`unmount`, videoRef)
        }
    }, [])

    return (
        <SafeAreaView style={style.page}>
            <StatusBar barStyle={barStyle} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    {/* <Video style={style.video} source={{uri: url, title: "ABC"}} /> */}
                    <DemoView ref={videoRef} style={{width: "100%", aspectRatio: 16/9, backgroundColor: "red"}} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}