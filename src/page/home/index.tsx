import { PropsWithNavigation } from '@global';
import { Device } from '@helper/device';
import { printException } from '@helper/log';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { fetchEmbyAlbumAsync, patchCurrentEmbySite } from '@store/embySlice';
import { selectThemedPageStyle } from '@store/themeSlice';
import { SiteResource } from '@view/SiteResource';
import { StatusBar } from '@view/StatusBar';
import React, {useEffect, useState} from 'react';
import {
    Button,
    RefreshControl,
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
    const dispatch = useAppDispatch()
    const pageStyle = useAppSelector(selectThemedPageStyle)
    useEffect(() => {
        if (!site?.server || !site?.user) {
            return
        }
        emby?.getPublicInfo?.().then(data => {
            console.log(`site: `, data.ServerName);
            dispatch(patchCurrentEmbySite({
                name: data.ServerName,
                version: data.Version,
            }))
        })
        .catch(printException)
        
    }, [site])

    const goToLogin = () => {
        navigation.navigate("login")
    }

    const [refreshing, setRefreshing] = useState(false)
    const onRefresh = () => {
        setRefreshing(true)
        dispatch(fetchEmbyAlbumAsync())
            .then(() => {
                setRefreshing(false)
            })
    }

    return (
        <View style={{...style.page, ...pageStyle}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={Device.isDesktop}
                nestedScrollEnabled={true}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                style={{flex: 1}}>
                {site?.server && site?.user ? <SiteResource /> : <Button title="添加站点" onPress={goToLogin} />}
            </ScrollView>
        </View>
    );
}
