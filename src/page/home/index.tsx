import { config } from '@api/config';
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
    View,
} from 'react-native';
import { useSelector } from 'react-redux';

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
        if (!site?.user) {
            return
        }
        Api.emby = new Emby(site?.user)
        Api.emby?.getPublicInfo().then(data => {
            console.log(data.ServerName);
        });
    }, [site?.user])

    return (
        <SafeAreaView style={style.page}>
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                {site ? <AlbumWidget /> : <Button title="添加站点" onPress={() => navigation.navigate("login")} />}
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    );
}
