import { Pressable, StyleSheet, View } from "react-native";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { useCallback } from "react";
import { getAvatarUrl } from "@store/embySlice";
import { Image } from "@view/Image";
import { DEFAULT_AVATOR_URL } from "@helper/image";
import { toggleSwitchSiteDialog } from "@store/menuSlice";

const hitSlop = {top: 10, bottom: 10, left: 10, right: 10}

const style = StyleSheet.create({
    root: {
        flexDirection: 'row',
        transform: [
            {translateY: 1}
        ]
    },
    avator: {
        width: 32,
        height: 32,
        borderWidth: 2,
        borderRadius: 16,
        borderColor: 'lightgray',
    }
})

export function HeaderRightAction() {
    const avatorUrl = useAppSelector(getAvatarUrl)
    const site = useAppSelector(state => state.emby?.site)
    const dispatch = useAppDispatch()
    const showSiteSelect = useCallback(() => {
        dispatch(toggleSwitchSiteDialog())
    }, [dispatch])
    if (!site) return null
    return (
        <View style={style.root}>
            <Pressable hitSlop={hitSlop} onPress={showSiteSelect}>
            <Image style={style.avator} 
                source={{uri: avatorUrl }}
                fallbackImages={[DEFAULT_AVATOR_URL]} />
            </Pressable>
        </View>
    );
}
