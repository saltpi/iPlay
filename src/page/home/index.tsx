import {Api} from '@api/emby';
import { NavigationProp, useNavigation } from '@react-navigation/native';
import {Album} from '@view/Album';
import React, {PropsWithChildren, useEffect, useState} from 'react';
import {
    SafeAreaView,
    ScrollView,
    StatusBar,
    Text,
    View,
    useColorScheme,
} from 'react-native';
import {Colors} from 'react-native/Libraries/NewAppScreen';

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
                        <Text>{serverName}</Text>
                        <Album />
                    </View>
                </View>
            </ScrollView>
        </SafeAreaView>
    );
}
