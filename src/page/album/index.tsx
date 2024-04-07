import {PropsWithNavigation} from '@global';
import {logger, printException} from '@helper/log';
import {useAppDispatch, useAppSelector} from '@hook/store';
import { fetchAlbumMediaAsync } from '@store/embySlice';
import {selectThemeBasicStyle, selectThemedPageStyle} from '@store/themeSlice';
import {ListView, kFullScreenStyle} from '@view/ListView';
import {MediaCard, MediaCardInLine} from '@view/MediaCard';
import {Spin} from '@view/Spin';
import _ from 'lodash';
import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import OrderIcon from "@asset/order.svg"
import LayoutIcon from "@asset/layout.svg"
import { EpisodeCard } from '@view/EpisodeCard';

enum LayoutType {
    Card,
    Line,
}

const style = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: 'column',
        flexWrap: 'wrap',
        alignItems: 'center',
        justifyContent: 'center',
    },
});


export function Page({ navigation, route}: PropsWithNavigation<'album'>) {
    const data = useAppSelector(state => state.emby?.source?.albumMedia?.[route.params.albumId]);
    const [loading, setLoading] = useState(true);
    const theme = useAppSelector(selectThemeBasicStyle);
    const [orderByAddTime, setOrderByAddTime] = useState(false);
    const [layoutWithCard, setLayoutWithCard] = useState(true);
    const pageStyle = useAppSelector(selectThemedPageStyle);
    const dispatch = useAppDispatch()
    const cached = data?.length ?? 0 > 0
    useEffect(() => {
        navigation.setOptions({
            headerRight: () => (
                <View style={{flexDirection: "row", alignItems: "center", justifyContent: "center"}}>
                    <OrderIcon onPress={() => setOrderByAddTime(t => !t)} 
                        width={22} style={{marginRight: 15, ...theme}} />
                    <LayoutIcon onPress={() => setLayoutWithCard(t => !t)}
                        width={22} style={{marginRight: 10, ...theme}} />
                </View>
            ),
        });
    }, []);

    useEffect(() => {
        setLoading(!cached);
        logger.info("fetch album media", route.params.albumId, orderByAddTime)
        const id = route.params.albumId;
        const query = {
            id, 
            options: {
                SortBy: orderByAddTime ? "DateCreated,SortName" : "DateLastContentAdded,SortName"
            }
        }
        dispatch(fetchAlbumMediaAsync(query))
            .then(() => {
                setLoading(false)
            })
            .catch(printException)
            .finally(() => setLoading(false));
    }, [route.params.albumId, orderByAddTime]);
    
    const onVisibleIndicesChanged = () => {
    };

    const onEndReached = () => {};

    const rowItemWidth =
        (kFullScreenStyle.width - 20) /
        Math.floor((kFullScreenStyle.width - 20) / 120);

    return (
        <View style={{...style.root, paddingTop: pageStyle.paddingTop}}>
            {data && data.length > 0 ? (
                <ListView
                    items={data}
                    style={{width: '100%', flex: 1, padding: 10}}
                    typeForIndex={i => layoutWithCard ? LayoutType.Card : LayoutType.Line}
                    layoutForType={(i, dim) => {
                        if (i === LayoutType.Card) {
                            dim.width = rowItemWidth;
                            dim.height = 200;
                        } else {
                            dim.width = kFullScreenStyle.width;
                            dim.height = 200;
                        }
                    }}
                    onVisibleIndicesChanged={onVisibleIndicesChanged}
                    onEndReached={onEndReached}
                    render={(media, i, type) => (
                        type == LayoutType.Card ?
                        <MediaCard
                            key={media?.Id ?? `media-${i}`}
                            media={media}
                            theme={theme}
                        /> :
                        <MediaCardInLine
                        key={media?.Id ?? `media-${i}`}
                        media={media}
                        theme={theme}
                        />
                    )}
                />
            ) : null}
            {loading ? <Spin color={theme.color} /> : null}
        </View>
    );
}
