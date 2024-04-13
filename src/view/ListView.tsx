import _ from "lodash";
import { Dimensions, ViewStyle } from "react-native";
import { DataProvider, Dimension, LayoutProvider, RecyclerListView } from "recyclerlistview";

export const kFullScreenStyle = { 
    width: Dimensions.get("window").width, 
    height: Dimensions.get("window").height 
}

export const ListBaseView = RecyclerListView

export interface MyListViewProps<T> {
    items: T[]
    render: (item: T, index: number, type: number | string) => JSX.Element
    style?: ViewStyle;
    typeForIndex?: (index: number) => number | string;
    layoutForType?: (type: string | number, dim: Dimension, index: number) => void
    isHorizontal?: boolean
    showsHorizontalScrollIndicator?: boolean
    showsVerticalScrollIndicator?: boolean
    isIdentity?: (a: T, b: T) => boolean
    onEndReached?: () => void
    renderFooter?: () => JSX.Element
    onVisibleIndicesChanged?: (indices: number[]) => void
}

export function ListView<T>({
    items,
    render,
    style = {},
    typeForIndex = (idx) => 0,
    layoutForType = (type, dim, index) => {
        dim.width = 120
        dim.height = 120
    },
    isHorizontal = false,
    showsHorizontalScrollIndicator = false,
    showsVerticalScrollIndicator = false,
    isIdentity = (a, b) => a === b,
    onEndReached,
    renderFooter,
    onVisibleIndicesChanged
}: MyListViewProps<T>) {
    const dataProvider = new DataProvider(isIdentity).cloneWithRows(items)
    
    const layoutProvider = new LayoutProvider(
        typeForIndex,
        layoutForType
    )

    return (
        <ListBaseView style={{...kFullScreenStyle, ...style}}
            isHorizontal={isHorizontal}
            showsHorizontalScrollIndicator={showsHorizontalScrollIndicator}
            showsVerticalScrollIndicator={showsVerticalScrollIndicator}
            scrollViewProps={{
                contentContainerStyle: {minWidth: "100%"}
            }}
            dataProvider={dataProvider} 
            layoutProvider={layoutProvider}
            onEndReached={onEndReached}
            renderFooter={renderFooter}
            onVisibleIndicesChanged={onVisibleIndicesChanged}
            rowRenderer={(type, data, i) => render(data, i, type)} />
    )
}