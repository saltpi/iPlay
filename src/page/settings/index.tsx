import { SettingItem, SettingItemProps } from "@view/settings/SettingItem";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { Navigation, PropsWithNavigation } from "@global";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { StatusBar } from "@view/StatusBar";
import { useAppSelector } from "@hook/store";
import { selectThemeBasicStyle } from "@store/themeSlice";

const style = StyleSheet.create({
    page: {
        flex: 1,
    }
});

const settings: SettingItemProps[] = [
    {
        icon: "Theme",
        label: "主题配置",
        onPress: (setting, navigation) => {
            navigation?.navigate("theme")
        }
    },
    {
        icon: "Site",
        label: "站点配置",
        onPress: (setting, navigation) => {
            navigation?.navigate("login")
        }
    },
    {
        icon: "Video",
        label: "视频设置",
        onPress: (setting, navigation) => {
            navigation?.navigate("config_video")
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
        icon: "Trash",
        label: "应用缓存",
    },
    {
        icon: "Mobile",
        label: "关于",
        onPress: (setting, navigation) => {
            navigation?.navigate("about")
        }
    },
];

export function Page({navigation}: PropsWithNavigation<"default">) {
    const insets = useSafeAreaInsets()
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const theme = useAppSelector(selectThemeBasicStyle)
    const onPress = (setting: SettingItemProps) => {
        if (setting.onPress) {
            setting.onPress.bind(null, setting, navigation)()
        } else {
            Toast.show({
                type: 'success',
                text2: '功能暂未开放',
                text2Style: {fontSize: 14, color: 'black'},
                position: 'top',
                topOffset: insets.top + 2.5,
            });
        }
    }
    return (
        <View style={{...style.page, backgroundColor}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    {settings.map((setting, idx) => 
                        <SettingItem key={idx} {...setting}
                            theme={theme} 
                            onPress={() => onPress(setting)} />)}
                </View>
            </ScrollView>
        </View>
    )
}