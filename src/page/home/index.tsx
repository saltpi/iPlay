import { config } from '@api/config';
import {Api} from '@api/emby';
import { PropsWithNavigation } from '@global';
import { useAppDispatch, useAppSelector } from '@store';
import { increment, incrementAsync, selectCount } from '@store/counterSlice';
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
    const counter = useAppSelector(selectCount)
    const dispatch = useAppDispatch()
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
            <Text>{counter}</Text>
            <Button title="+1" onPress={() => dispatch(increment())}/>
            <MenuBar />
        </SafeAreaView>
    );
}
