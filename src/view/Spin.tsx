import _ from "lodash";
import { ComponentProps } from "react";
import { ActivityIndicator, StyleSheet, View } from "react-native";

export type SpinProps = ComponentProps<typeof ActivityIndicator>;

const style = StyleSheet.create({
    center: {
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: [{translateX: -10}, {translateY: -10}],
    },
    box: {
        backgroundColor: "transparent",
        paddingTop: 10,
        alignItems: "center",
        justifyContent: "center",
    }
});

export function Spin({style: custom, ...rest}: SpinProps) {
    return (
        <ActivityIndicator style={style.center}
            size={"small"} 
            color="#0f0f0f" {...rest} />
    )
}

export function SpinBox({style: custom, ...rest}: SpinProps) {
    return (
        <View style={style.box}>
            <ActivityIndicator style={style.center}
                size={"small"} 
                color="#0f0f0f" {...rest} />
        </View>
    )
}
