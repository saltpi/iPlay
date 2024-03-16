import {Api} from '@api/emby';
import { NavigationProp, useNavigation } from '@react-navigation/native';
import {AlbumWidget} from '@view/AlbumList';
import { MenuBar } from '@view/menu/MenuBar';
import React, {PropsWithChildren, useEffect, useState} from 'react';
import {
    SafeAreaView,
    ScrollView,
    StatusBar,
    StyleSheet,
    Text,
    View,
    useColorScheme,
} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';

const style = StyleSheet.create({
    page: {
        flex: 1,
    },
    sectionTitle: {
        fontSize: 24,
        fontWeight: '600',
    }
});

export function Page() {
    const isDarkMode = useColorScheme() === 'dark';
    const backgroundStyle = {
        backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    };
    const [serverName, setServerName] = useState('');
    useEffect(() => {
        Api.emby?.getPublicInfo().then(data => {
            console.log(data.ServerName);
            setServerName(data.ServerName);
        });
    }, [Api.emby]);
    return (
        <SafeAreaView style={{...backgroundStyle, ...style.page}}>
            <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                showsHorizontalScrollIndicator={false}
                showsVerticalScrollIndicator={false}
                style={{...backgroundStyle, flex: 1}}>
                <View style={backgroundStyle}>
                    <AlbumWidget />
                </View>
            </ScrollView>
            <MenuBar />
        </SafeAreaView>
    );
}
