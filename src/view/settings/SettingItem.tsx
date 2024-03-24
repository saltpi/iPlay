import { StyleSheet, Text, TouchableHighlight, View, useColorScheme } from "react-native";
import { Image } from '@view/Image';
import { Navigation } from "@global";
import VideoIcon from "@asset/video.svg"
import EarphoneIcon from "@asset/earphone.svg"
import TrashIcon from "@asset/trash.svg"
import MessageIcon from "@asset/message.svg"
import MobileIcon from "@asset/phone.svg"
import ThemeIcon from "@asset/paint.svg"
import Indicator from "@asset/right.arrow.svg"
import SiteIcon from "@asset/database-server.svg"
import { useAppSelector } from "@hook/store";


const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        padding: 10,
        paddingLeft: 20,
        paddingRight: 20,
        alignItems: "center",
        backgroundColor: "white",
        borderTopColor: "lightgray",
        borderTopWidth: 0.25,
    },
    label: {
        fontWeight: "300",
    },
    icon: {
        width: 24,
        aspectRatio: 1,
        marginRight: 10,
        objectFit: "contain"
    },
    indicator: {
        position: "absolute",
        right: 20,
        objectFit: "contain",
        aspectRatio: 1,
        width: 20,
    }
});

export const Icon = {
    Site: SiteIcon,
    Video: VideoIcon,
    Audio: EarphoneIcon,
    Trash: TrashIcon,
    Message: MessageIcon,
    Indicator: Indicator,
    Mobile: MobileIcon,
    Theme: ThemeIcon,
}

export type IconType = keyof typeof Icon;

export interface SettingItemProps {
    icon: IconType;
    label: string;
    onPress?: (setting?: SettingItemProps, navigation?: Navigation) => void;
}

export function SettingItem(props: SettingItemProps) {
    const SettingIcon = Icon[props.icon];
    const color = useAppSelector(state => state.theme.fontColor);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    return (
        <TouchableHighlight onPress={() => props.onPress?.()} underlayColor={backgroundColor}>
        <View style={{...style.root, backgroundColor}}>
            {SettingIcon ? <SettingIcon
                width={style.icon.width}
                style={style.icon} /> : null}
            <Text style={{...style.label, color}}>{props.label}</Text>
            <Icon.Indicator width={style.indicator.width} 
                style={style.indicator} />
        </View>
        </TouchableHighlight>
    )
}