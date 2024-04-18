import OrderIcon from "@asset/order.svg"
import LayoutDrawerIcon from "@asset/layout_drawer.svg"
import LayoutBurgerIcon from "@asset/layout_burger.svg"
import { Pressable, StyleSheet, View } from "react-native";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { LayoutType, selectThemeBasicStyle, updateToNextAlbumLayoutType } from "@store/themeSlice";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useCallback } from "react";
import { SortType, getAvatarUrl, updateToNextAlbumSortType } from "@store/embySlice";
import { Image } from "@view/Image";
import { DEFAULT_AVATOR_URL } from "@helper/image";
import { toggleSwitchSiteDialog } from "@store/menuSlice";

const hitSlop = {top: 10, bottom: 10, left: 10, right: 10}

const style = StyleSheet.create({
    root: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
    avator: {
        width: 48,
        height: 48,
        marginBottom: 4,
        borderWidth: 2,
        borderRadius: 24,
        borderColor: 'lightgray',
    }
})

export function HeaderRightAction() {
    const theme = useAppSelector(selectThemeBasicStyle);
    const avatorUrl = useAppSelector(getAvatarUrl)
    const dispatch = useAppDispatch()
    const showSiteSelect = useCallback(() => {
        dispatch(toggleSwitchSiteDialog())
    }, [dispatch])
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
