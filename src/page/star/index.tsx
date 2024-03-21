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
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}