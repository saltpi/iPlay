import { PropsWithNavigation } from '@global';
import { useAppSelector } from '@hook/store';
import {SiteResource} from '@view/AlbumList';
import { StatusBar } from '@view/StatusBar';
import React, {useEffect} from 'react';
import {
    Button,
    SafeAreaView,
    ScrollView,
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
    const site = useAppSelector(state => state.emby?.site)
    const emby = useAppSelector(state => state.emby?.emby)
    const theme = useAppSelector(state => state.theme)
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
            <StatusBar />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{flex: 1}}>
                <View style={{marginBottom: theme.menuBarHeight}}>
                {site?.server && site?.user ? <SiteResource site={site} /> : <Button title="添加站点" onPress={goToLogin} />}
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}
