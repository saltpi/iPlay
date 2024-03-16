import {Api} from '@api/emby';
import { NavigationProp, useNavigation } from '@react-navigation/native';
import {AlbumWidget} from '@view/AlbumList';
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
        <SafeAreaView style={backgroundStyle}>
            <StatusBar
                barStyle={isDarkMode ? 'light-content' : 'dark-content'}
                backgroundColor={backgroundStyle.backgroundColor}
            />
            <ScrollView
                contentInsetAdjustmentBehavior="automatic"
                style={backgroundStyle}>
                <View
                    style={{
                        backgroundColor: isDarkMode
                            ? Colors.black
                            : Colors.white,
                    }}>
                    <View>
                        <Text style={style.sectionTitle}>{serverName}</Text>
                        <AlbumWidget />
                    </View>
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}
