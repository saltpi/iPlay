import {Api, Emby} from '@api/emby';
import { PropsWithNavigation } from '@global';
import { useAppSelector } from '@hook/store';
import {AlbumWidget} from '@view/AlbumList';
import { MenuBar } from '@view/menu/MenuBar';
import React, {useEffect} from 'react';
import {
    Button,
    SafeAreaView,
    ScrollView,
    StatusBar,
    StyleSheet,
    Text,
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
    const site = useAppSelector(state => state.emby?.site)
    
    useEffect(() => {
        if (!site?.server || !site?.user) {
            return
        }
        console.log(`home site update`, site)
        Api.emby = new Emby(site)
        Api.emby?.getPublicInfo?.().then(data => {
            console.log(data.ServerName);
        });
    }, [site])

    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                {site?.server && site?.user ? <AlbumWidget /> : <Button title="添加站点" onPress={() => navigation.navigate("login")} />}
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    );
}
