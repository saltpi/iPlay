import { StatusBar } from "@view/StatusBar";
import { SafeAreaView, ScrollView, StyleSheet, View } from "react-native";
import { useEffect, useRef } from "react";
import { useAppSelector } from "@hook/store";
import { Video } from "@view/Video";
import { selectThemedPageStyle } from "@store/themeSlice";
import { Dev } from "@helper/dev";

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    video: {
        width: "100%",
        aspectRatio: 16/9,
    }
});

export function Page() {
    const ref= useRef<any>(null);
    const videoRef = useRef<any>(null);
    const pageStyle = useAppSelector(selectThemedPageStyle)
    const url = Dev.videoUrl
    
    useEffect(() => {
        return () => {
            console.log(`unmount`, ref.current)
            console.log(`unmount`, videoRef)
        }
    }, [])

    return (
        <View style={{...style.page, ...pageStyle}}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <Video style={style.video} source={{uri: url, title: "Breaking Bad"}} />
                </View>
            </ScrollView>
        </View>
    )
}