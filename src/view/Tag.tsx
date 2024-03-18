import React, { PropsWithChildren } from "react";
import { View, Text, StyleSheet, Touchable, TouchableOpacity, GestureResponderEvent } from "react-native";

const COLOR = {
    blue: {
        backgroundColor: '#e6f4ff',
        borderColor: '#91caff',
        color: '#0958d9',
    },
    cyan: {
        backgroundColor: '#e6fffb',
        borderColor: '#87e8de',
        color: '#08979c',
    },
    geekblue: {
        backgroundColor: '#f0f5ff',
        borderColor: '#adc6ff',
        color: '#1d39c4',
    },
    gold: {
        backgroundColor: '#fffbe6',
        borderColor: '#ffe58f',
        color: '#d48806',
    },
    green: {
        backgroundColor: '#f6ffed',
        borderColor: '#b7eb8f',
        color: '#389e0d',
    },
    lime: {
        backgroundColor: '#fcffe6',
        borderColor: '#eaff8f',
        color: '#7cb305',
    },
    magenta: {
        backgroundColor: '#fff0f6',
        borderColor: '#ffadd2',
        color: '#c41d7f',
    },
    orange: {
        backgroundColor: '#fff7e6',
        borderColor: '#ffd591',
        color: '#d46b08',
    },
    pink: {
        backgroundColor: '#fff0f6',
        borderColor: '#ffadd2',
        color: '#c41d7f',
    },
    purple: {
        backgroundColor: '#f9f0ff',
        borderColor: '#d3adf7',
        color: '#531dab',
    },
    red: {backgroundColor: '#fff1f0', borderColor: '#ffa39e', color: '#cf1322'},
    volcano: {
        backgroundColor: '#fff2e8',
        borderColor: '#ffbb96',
        color: '#d4380d',
    },
    yellow: {
        backgroundColor: '#feffe6',
        borderColor: '#fffb8f',
        color: '#d4b106',
    },
};

const style = StyleSheet.create({
    root: {
        display: 'flex',
        padding: 2.5,
        width: 'auto',
        marginTop: 2.5,
        marginLeft: 2.5,
        marginRight: 2.5,
        marginBottom: 2,
        borderRadius: 4,
        borderWidth: 1,
    },
    text: {
        width: "auto"
    }
})

export type TagProps = PropsWithChildren<{
    color?: keyof typeof COLOR;
    onPress?: (event: GestureResponderEvent) => void
}>

export function Tag(props: TagProps) {
    let color = props.color
    if (!props.color) {
        const keys = Object.keys(COLOR)
        const key = keys[Math.floor(Math.random() * keys.length)];
        color = key as keyof typeof COLOR
    }
    return (
        <TouchableOpacity onPress={props.onPress} activeOpacity={1.0}>
        <View style={{...COLOR[color!], ...style.root}}>
            <Text style={{...style.text, color: COLOR[color!].color}}>
                {props.children}
            </Text>
        </View>
        </TouchableOpacity>
    )
}