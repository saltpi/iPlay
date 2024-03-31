import {StyleSheet, Text, TouchableOpacity, View, ViewStyle} from 'react-native';
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
    count: number;
    style?: Partial<ViewStyle>;
}

export function PlayCount({count}: PlayCountProps) {
    return (
        <View style={{...style.root, height: style.icon.height}}>
            <ViewShowIcon width={style.icon.width} 
                style={style.icon} />
            <Text>{count}</Text>
        </View>
    );
}
