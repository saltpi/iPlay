import { StatusBar } from "@view/StatusBar";
import { MenuBar } from "@view/menu/MenuBar";
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";

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
            <MenuBar />
        </SafeAreaView>
    )
}