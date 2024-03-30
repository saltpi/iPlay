import { PropsWithNavigation } from "@global";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { selectThemeBasicStyle, updateMenuBarPaddingOffset, updateShowVideoLink, updateTheme } from "@store/themeSlice";
import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, Switch, Text, TextInput, View } from "react-native";

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

export function Page({navigation}: PropsWithNavigation<"theme">) {
    const dispatch = useAppDispatch();
    const menuBarPaddingOffset = useAppSelector((state) => state.theme.menuBarPaddingOffset);
    const showVideoLink = useAppSelector((state) => state.theme.showVideoLink);
    const headerTitleAlign = useAppSelector((state) => state.theme.headerTitleAlign);
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)

    const updateTitleAlign = () => {
        dispatch(updateTheme(s => {
            s.headerTitleAlign = s.headerTitleAlign === 'center' ? 'left' : 'center';
            return s
        }));
    }
    return (
        <SafeAreaView style={{...style.page, backgroundColor}}>
            <StatusBar translucent={false} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View style={style.inline}>
                    <Text style={{...style.label, ...theme}}>菜单栏下边距</Text>
                    <TextInput style={{...style.input, color}}
                        keyboardType="numeric"
                        value={menuBarPaddingOffset.toString()}
                        onChangeText={(text) => dispatch(updateMenuBarPaddingOffset(Number(text)))}
                        />
                </View>
                <View style={style.inline}>
                    <Text style={{...style.label, ...theme}}>显示视频链接</Text>
                    <Switch value={showVideoLink}
                        onChange={() => { dispatch(updateShowVideoLink(!showVideoLink)) }} />
                </View>
                <View style={style.inline}>
                    <Text style={{...style.label, ...theme}}>导航栏标题居中(默认居左, 仅安卓)</Text>
                    <Switch value={headerTitleAlign === 'center'}
                        onChange={updateTitleAlign} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}