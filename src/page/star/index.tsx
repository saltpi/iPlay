import { Navigation, PropsWithNavigation } from "@global";
import { NavigationProp } from "@react-navigation/native";
import { StatusBar } from "@view/StatusBar";
import { Button, SafeAreaView, ScrollView, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});

export function Page(props: PropsWithNavigation<"default">) {
    const { navigation } = props
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