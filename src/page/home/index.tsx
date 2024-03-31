import { PropsWithNavigation } from '@global';
import { OSType, isIOS, isOS } from '@helper/device';
import { printException } from '@helper/log';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { patchCurrentEmbySite, updateCurrentEmbySite } from '@store/embySlice';
import { selectThemedPageStyle } from '@store/themeSlice';
import {SiteResource} from '@view/AlbumList';
import { StatusBar } from '@view/StatusBar';
import { set } from 'lodash';
import React, {useEffect, useState} from 'react';
import {
    Button,
    RefreshControl,
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
    const dispatch = useAppDispatch()
    const theme = useAppSelector(state => state.theme)
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const [etag, setEtag] = useState(Date.now().toString())
    const pagePaddingTop = useAppSelector(state => state.theme.pagePaddingTop)
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
        setEtag(Date.now().toString())
        setTimeout(() => {
            setRefreshing(false)
        }, 500)
    }

    return (
        <View style={{...style.page, ...pageStyle}}>
            <StatusBar />
            <ScrollView
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                style={{flex: 1}}>
                {site?.server && site?.user ? <SiteResource etag={etag} /> : <Button title="添加站点" onPress={goToLogin} />}
            </ScrollView>
        </View>
    );
}
