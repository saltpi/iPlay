import { IconType, SettingItem, SettingItemProps } from "@view/settings/SettingItem";
import { MenuBar } from "@view/menu/MenuBar";
import { SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, View } from "react-native";
import { PropsWithNavigation } from "@global";
import { Toast } from "@helper/toast";
import { useSafeAreaFrame, useSafeAreaInsets } from "react-native-safe-area-context";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});

const settings: SettingItemProps[] = [
    {
        icon: "Video",
        label: "视频设置",
        onPress: () => {}
    },
    {
        icon: "Audio",
        label: "音频设置",
        onPress: () => {}
    },
    {
        icon: "Message",
        label: "最近消息",
        onPress: () => {}
    },
    {
        icon: "Mobile",
        label: "关于",
        onPress: () => {}
    },
    {
        icon: "Trash",
        label: "应用缓存",
        onPress: () => {}
    },
];

export function Page({navigation}: PropsWithNavigation<"default">) {
    const insets = useSafeAreaInsets()
    const onPress = (setting: SettingItemProps) => {
        if (setting.icon === "Video") {
            navigation.navigate("login");
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
            <StatusBar barStyle={"dark-content"} />
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
            <MenuBar />
        </SafeAreaView>
    )
}