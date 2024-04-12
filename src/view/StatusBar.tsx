import { Device, OSType, isOS } from "@helper/device";
import { useAppSelector } from "@hook/store";
import { ComponentProps } from "react";
import { Platform, StatusBar as StatusBarOrigin, NativeModules, Text } from "react-native";

export const StatusBarHeight = 
    Platform.OS === "android" ? (StatusBarOrigin.currentHeight ?? 0) : (isOS(OSType.Windows) ? 0 :NativeModules.UIModule.statusBarHeight());

export function MobileStatusBar(props: ComponentProps<typeof StatusBarOrigin>) {
    const barStyle = useAppSelector(state => state.theme.barStyle);
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    return (
        <StatusBarOrigin 
            barStyle={barStyle} 
            backgroundColor={"transparent"}
            translucent
            {...props} />
    )
}

export function DesktopStatusBar(props: ComponentProps<typeof StatusBarOrigin>) {
    return (
        <Text>GoBack</Text>
    );
}

export const StatusBar = Device.isMobile ? MobileStatusBar : DesktopStatusBar;