import { useAppSelector } from "@hook/store";
import { ComponentProps } from "react";
import { StatusBar as StatusBarOrigin } from "react-native";

export function StatusBar(props: ComponentProps<typeof StatusBarOrigin>) {
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