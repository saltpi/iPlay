import { PropsWithNavigation } from "@global";
import { useAppSelector } from "@hook/store";
import { Button, SafeAreaView, ScrollView, StatusBar, StyleSheet, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    loading: {

    }
});

export function Page(props: PropsWithNavigation<"default">) {
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    return (
        <SafeAreaView style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                    <Button title="test" onPress={() => {props.navigation.navigate("test")}} />
            </ScrollView>
        </SafeAreaView>
    )
}