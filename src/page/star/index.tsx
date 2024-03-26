import { PropsWithNavigation } from "@global";
import { windowWidth } from "@helper/device";
import { useAppSelector } from "@hook/store";
import { MPVPlayer, MPVPlayerView } from "@view/MPVPlayer";
import { StatusBar } from "@view/StatusBar";
import { Dimensions, SafeAreaView, ScrollView, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});


export function Page(props: PropsWithNavigation<"default">) {
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const url = "https://drive.endemy.me/iplay/demo.mp4"
    return (
        <SafeAreaView style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <MPVPlayerView style={{width: 1440, height: 810}}
                        url={url}
                     />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}