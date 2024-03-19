import { SettingItem, SettingItemProps } from "@view/settings/SettingItem";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { Navigation, PropsWithNavigation } from "@global";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { StatusBar } from "@view/StatusBar";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});

const settings: SettingItemProps[] = [
    {
        icon: "Video",
        label: "主题配置",
        onPress: (setting, navigation) => {
            navigation?.navigate("theme")
        }
    },
    {
        icon: "Video",
        label: "视频设置",
        onPress: (setting, navigation) => {
            navigation?.navigate("login")
        }
    },
    {
        icon: "Audio",
        label: "音频设置",
    },
    {
        icon: "Message",
        label: "最近消息",
    },
    {
        icon: "Mobile",
        label: "关于",
    },
    {
        icon: "Trash",
        label: "应用缓存",
    },
];

export function Page({navigation}: PropsWithNavigation<"default">) {
    const insets = useSafeAreaInsets()
    const onPress = (setting: SettingItemProps) => {
        if (setting.onPress) {
            setting.onPress.bind(null, setting, navigation)()
        } else {
            Toast.show({
                type: 'success',
                text2: '功能暂未开放',
                text2Style: {fontSize: 14, color: 'black'},
                position: 'top',
                topOffset: insets.top,
            });
        }
    }
    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    {settings.map((setting, idx) => 
                        <SettingItem key={idx} {...setting} onPress={() => onPress(setting)} />)}
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}