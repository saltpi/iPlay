import { PropsWithNavigation } from "@global";
import { IntentModule } from "@helper/native";
import { StatusBar } from "@view/StatusBar";
import { Button, SafeAreaView, ScrollView, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});


export function Page(props: PropsWithNavigation<"default">) {
    const { navigation } = props
    const playVideo = () => {
        IntentModule.playFile("https://drive.endemy.me/iplay/demo.mp4")
    }
    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <Button title="播放视频" onPress={playVideo} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}