import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import PlayerView from "@view/Player"

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});

export function Page() {
    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    {/* <PlayerView style={{width: "100%", aspectRatio: 16/9}}
                        bgcolor="e0e0e0"
                        url="http://tkremby.xyz:2095/emby/videos/417367/original.mkv?DeviceId=feed8217-7abd-4d2d-a561-ed21c0b9c30e&MediaSourceId=da699410b4136abee1c65b10cc5d9c9a&PlaySessionId=a595fdb596ea4df3b8a408bcee976ec5&api_key=d639bafb9edd4e0c84f37532c9913f32"
                     /> */}
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}