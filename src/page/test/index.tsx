import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { useEffect, useRef } from "react";
import { useAppSelector } from "@hook/store";
import { Video } from "@view/Video";

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
    const url = "https://drive.endemy.me/breaking-bad.mp4"
    useEffect(() => {
        return () => {
            console.log(`unmount`, ref.current)
            console.log(`unmount`, videoRef)
        }
    }, [])

    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <Video style={style.video} source={{uri: url, title: "Breaking Bad"}} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}