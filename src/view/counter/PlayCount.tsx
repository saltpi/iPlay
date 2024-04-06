import {StyleProp, StyleSheet, Text, TextStyle, TouchableOpacity, View, ViewStyle} from 'react-native';
import ViewShowIcon from "@asset/view-show.svg"

const style = StyleSheet.create({
    root: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "center",
        marginRight: 2.5,
        marginLeft: 2.5,
    },
    icon: {
        width: 35,
        height: 35,
        flexShrink: 0,
        flexGrow: 0,
        marginRight: 2.5,
    },
});

export interface PlayCountProps {
    width?: number;
    count: number;
    style?: TextStyle;
}

export function PlayCount({count, width = style.icon.width, style: extraStyle}: PlayCountProps) {
    return (
        <View style={{...style.root, height: style.icon.height}}>
            <ViewShowIcon width={width} 
                style={style.icon} />
            <Text style={{color: extraStyle?.color}}>{count}</Text>
        </View>
    );
}
