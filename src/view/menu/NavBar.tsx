import { useAppSelector } from "@hook/store";
import { useNavigation } from "@react-navigation/native";
import { selectThemedPageStyle } from "@store/themeSlice";
import { useMemo } from "react";
import { Pressable, StyleSheet, Text, ViewStyle } from "react-native";

const style = StyleSheet.create({
    nav: {
        position: "absolute",
        top: 0,
        left: 0,
    }
})

export function NavBar() {
    const navigation = useNavigation()
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const layout = useMemo(() => ({
        nav: {
            ...style.nav,
            width: "100%",
            height: 100,
            backgroundColor: "red",
        } as ViewStyle
    }), [pageStyle])
    return (
        <Pressable style={layout.nav}
            onPress={() => navigation.goBack()}>
            <Text style={{color: pageStyle.color}}>GoBack Button</Text>
        </Pressable>
    )
}