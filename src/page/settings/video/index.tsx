import { useAppDispatch, useAppSelector } from "@hook/store";
import { updateConfig } from "@store/configSlice";
import { updateMenuBarPaddingOffset, updateShowVideoLink } from "@store/themeSlice";
import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, Switch, SwitchChangeEvent, Text, TextInput, View } from "react-native";

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
        flex: 1,
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
    const barStyle = useAppSelector(state => state.theme.barStyle);
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const dispatch = useAppDispatch();
    const useMpv = useAppSelector((state) => state.config.video.useInternalMPV);
    const toggleUseMpv = (event: SwitchChangeEvent) => {
        dispatch(updateConfig(s => {
            s.video.useInternalMPV = !s.video.useInternalMPV
            return s
        }))
    }

    return (
        <SafeAreaView style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1, backgroundColor}}>
                <View style={style.inline}>
                    <Text style={{...style.label, color}}>使用内置MPV播放器(Android)</Text>
                    <Switch value={useMpv}
                        onChange={toggleUseMpv} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}