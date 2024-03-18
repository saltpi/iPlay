import {Api, Emby} from '@api/emby';
import { PropsWithNavigation } from '@global';
import { useAppSelector } from '@hook/store';
import {SiteResource} from '@view/AlbumList';
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
    const emby = useAppSelector(state => state.emby?.emby)
    useEffect(() => {
        if (!site?.server || !site?.user) {
            return
        }
        emby?.getPublicInfo?.().then(data => {
            console.log(data.ServerName);
        });
    }, [site])

    const goToLogin = () => {
        navigation.navigate("login")
    }

    return (
        <SafeAreaView style={style.page}>
            <StatusBar barStyle={"dark-content"} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View>
                {site?.server && site?.user ? <SiteResource site={site} /> : <Button title="添加站点" onPress={goToLogin} />}
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    );
}
