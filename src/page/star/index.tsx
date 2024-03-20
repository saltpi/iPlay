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
                    <PlayerView style={{width: "100%", aspectRatio: 16/9}}
                        bgcolor="e0e0e0"
                        url="https://drive.ourfor.top/iplay/demo.mp4"
                     />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}