import _ from "lodash";
import { ComponentProps } from "react";
import { ActivityIndicator, StyleSheet } from "react-native";

export type SpinProps = ComponentProps<typeof ActivityIndicator>;

const style = StyleSheet.create({
    center: {
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: [{translateX: -10}, {translateY: -10}],
    }
});

export function Spin({style: custom, ...rest}: SpinProps) {
    return (
        <ActivityIndicator style={style.center}
            size={"small"} 
            color="#0f0f0f" {...rest} />
    )
}
