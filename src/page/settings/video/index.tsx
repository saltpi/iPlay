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
        minWidth: 150,
    }
});

export function Page() {
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const dispatch = useAppDispatch();
    const maxStreamingBitrate = useAppSelector((state) => state.config.video.MaxStreamingBitrate);
    const updateMaxStreamingBitrate = (text: string) => {
        dispatch(updateConfig(s => {
            s.video.MaxStreamingBitrate = Number(text);
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
                    <Text style={{...style.label, color}}>视频流比特率</Text>
                    <TextInput style={{...style.input, color}}
                        keyboardType="numeric"
                        value={maxStreamingBitrate?.toString()}
                        onChangeText={updateMaxStreamingBitrate}
                        />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}