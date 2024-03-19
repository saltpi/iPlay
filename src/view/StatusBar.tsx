import { ComponentProps } from "react";
import { StatusBar as StatusBarOrigin } from "react-native";

export function StatusBar(props: ComponentProps<typeof StatusBarOrigin>) {
    return (
        <StatusBarOrigin 
            barStyle={"dark-content"} 
            backgroundColor="white"
            {...props} />
    )
}