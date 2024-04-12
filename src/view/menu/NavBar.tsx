import { useAppSelector } from "@hook/store";
import { useNavigation } from "@react-navigation/native";
import { selectThemedPageStyle } from "@store/themeSlice";
import { useMemo } from "react";
import { Pressable, StyleSheet, Text, ViewStyle } from "react-native";
import GoBackIcon from "@asset/reset.svg"

const style = StyleSheet.create({
    nav: {
        backgroundColor: "transparent",
        position: "absolute",
        right: 0,
        top: "50%",
        width: 36,
        flexDirection: "column"
    }
})

export function NavBar() {
    const navigation = useNavigation()
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const layout = useMemo(() => ({
        nav: {
            ...style.nav,
        } as ViewStyle
    }), [pageStyle])

    return (
        <Pressable style={layout.nav}
            onPress={() => navigation.canGoBack() && navigation.goBack()}>
            <GoBackIcon width={36} />
        </Pressable>
    )
}