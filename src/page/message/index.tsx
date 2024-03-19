import { SafeAreaView, ScrollView, StatusBar, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    loading: {

    }
});

export function Page() {
    return (
        <SafeAreaView style={style.page}>
            <StatusBar barStyle={"dark-content"} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
            </ScrollView>
        </SafeAreaView>
    )
}