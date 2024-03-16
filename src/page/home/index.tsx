import { config } from '@api/config';
import {Api} from '@api/emby';
import { PropsWithNavigation } from '@global';
import {AlbumWidget} from '@view/AlbumList';
import { MenuBar } from '@view/menu/MenuBar';
import React, {useEffect} from 'react';
import {
    SafeAreaView,
    ScrollView,
    StatusBar,
    StyleSheet,
    View,
} from 'react-native';

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    sectionTitle: {
        fontSize: 24,
        fontWeight: '600',
    }
});

export function Page({navigation}: PropsWithNavigation<'home'>) {
    useEffect(() => {
        if (!Api.emby) {
            navigation.navigate("login")
            return
        } 
        Api.emby?.getPublicInfo().then(data => {
            console.log(data.ServerName);
        });
    }, [Api.emby]);

    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                    <AlbumWidget key={config?.emby?.host ?? "nil"} />
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    );
}
