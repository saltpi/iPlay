import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { VLCPlayer, Video } from "@view/Video";
import { useEffect, useRef } from "react";
import { useAppSelector } from "@hook/store";
import { MPVPlayer, MPVPlayerView } from "@view/MPVPlayer";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    video: {
        width: 1440,
        height: 810,
        aspectRatio: 16/9,
    }
});

export function Page() {
    const ref= useRef<any>(null);
    const barStyle = useAppSelector(state => state.theme.barStyle);
    const url = "https://drive.endemy.me/iplay/h265.mp4"
    useEffect(() => {
        return () => {
            console.log(`unmount`, ref.current)
        }
    }, [ref.current])
    return (
        <SafeAreaView style={style.page}>
            <StatusBar barStyle={barStyle} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <MPVPlayerView style={style.video} url={url} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}