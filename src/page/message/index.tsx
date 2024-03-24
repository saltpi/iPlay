import { useAppSelector } from "@hook/store";
import { SafeAreaView, ScrollView, StatusBar, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    loading: {

    }
});

export function Page() {
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    return (
        <SafeAreaView style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
            </ScrollView>
        </SafeAreaView>
    )
}