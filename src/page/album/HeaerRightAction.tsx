import OrderIcon from "@asset/order.svg"
import LayoutDrawerIcon from "@asset/layout_drawer.svg"
import LayoutBurgerIcon from "@asset/layout_burger.svg"
import { View } from "react-native";
import { useAppDispatch, useAppSelector } from "@hook/store";
import { LayoutType, selectThemeBasicStyle, updateTheme } from "@store/themeSlice";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useCallback } from "react";
import { SortType, updateAlbumSortType, updateToNextAlbumSortType } from "@store/embySlice";

export function HeaderRightAction() {
    const theme = useAppSelector(selectThemeBasicStyle);
    const layoutType = useAppSelector(state => state.theme.albumLayoutType);
    const sortType = useAppSelector(state => state.emby.sortType);
    const dispatch = useAppDispatch()
    const insets = useSafeAreaInsets()
    const updateSortType = useCallback(() => {
        console.log(`sort type: ${sortType}`)
        const newSortType = sortType ? (sortType + 1)%4 : SortType.NameAsc
        let newSortTypeName = ""
        switch(newSortType) {
            case SortType.AddedDateAsc: {
                newSortTypeName = "加入时间顺序"
                break
            }
            case SortType.AddedDateDesc: {
                newSortTypeName = "加入时间逆序"
                break
            }
            case SortType.NameAsc: {
                newSortTypeName = "名称顺序"
                break
            }
            case SortType.NameDesc: {
                newSortTypeName = "名称逆序"
                break
            }
        }
        Toast.show({
            type: 'success',
            text1: `按照 ${newSortTypeName} 排序`,
            position: 'top',
            topOffset: insets.top + 2.5, 
        });
        dispatch(updateToNextAlbumSortType());
    }, [dispatch, sortType])
    return (
        <View
            style={{
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'center',
            }}>
            <OrderIcon
                onPress={updateSortType}
                width={22}
                style={{marginRight: 15, ...theme}}
            />
            {layoutType === LayoutType.Line ?
            <LayoutDrawerIcon
                onPress={() => dispatch(updateTheme({albumLayoutType: LayoutType.Card}))}
                width={22}
                style={{marginRight: 10, ...theme}}
            />
            :
            <LayoutBurgerIcon
                onPress={() => dispatch(updateTheme({albumLayoutType: LayoutType.Line}))}
                width={22}
                style={{marginRight: 10, ...theme}}
            />
            }   
        </View>
    );
}
