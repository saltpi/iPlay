import { useAppDispatch, useAppSelector } from "@hook/store";
import { updateMenuBarPaddingOffset } from "@store/themeSlice";
import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    inline: {
        flexDirection: "row",
        alignItems: "center",
        paddingLeft: 10,
        paddingRight: 10,
        paddingTop: 10,
        paddingBottom: 8,
        borderBottomColor: "lightgray",
        borderBottomWidth: 0.25,
    },
    label: {
        fontSize: 16,
    },
    input: {
        fontSize: 16,
        padding: 5,
        paddingLeft: 10,
        paddingRight: 10,
        borderWidth: 1,
        borderColor: "lightgray",
        borderRadius: 5,
        marginLeft: 10,
        minWidth: 50,
    }
});

export function Page() {
    const dispatch = useAppDispatch();
    const menuBarPaddingOffset = useAppSelector((state) => state.theme.menuBarPaddingOffset);
    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View style={style.inline}>
                    <Text style={style.label}>菜单栏下边距</Text>
                    <TextInput style={style.input}
                        keyboardType="numeric"
                        value={menuBarPaddingOffset.toString()}
                        onChangeText={(text) => dispatch(updateMenuBarPaddingOffset(Number(text)))}
                        />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}