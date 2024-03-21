import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { VLCPlayer, Video } from "@view/Video";
import { useEffect, useRef } from "react";

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
    console.log(ref)
    useEffect(() => {
        return () => {
            console.log(`unmount`, ref.current)
        }
    }, [ref.current])

    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <VLCPlayer ref={ref} source={{uri: "https://drive.endemy.me/iplay/demo.mp4"}}  />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}