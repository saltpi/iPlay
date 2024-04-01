import {PropsWithNavigation} from '@global';
import {printException} from '@helper/log';
import {useAppDispatch, useAppSelector} from '@hook/store';
import { fetchAlbumMediaAsync } from '@store/embySlice';
import {selectThemeBasicStyle, selectThemedPageStyle} from '@store/themeSlice';
import {ListView, kFullScreenStyle} from '@view/ListView';
import {MediaCard} from '@view/MediaCard';
import {Spin} from '@view/Spin';
import _ from 'lodash';
import React, {useEffect, useState} from 'react';
import {StyleSheet, View} from 'react-native';

const style = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: 'column',
        flexWrap: 'wrap',
        alignItems: 'center',
        justifyContent: 'center',
    },
});


export function Page({route}: PropsWithNavigation<'album'>) {
    const data = useAppSelector(state => state.emby?.source?.albumMedia?.[route.params.albumId]);
    const [loading, setLoading] = useState(true);
    const theme = useAppSelector(selectThemeBasicStyle);
    const pageStyle = useAppSelector(selectThemedPageStyle);
    const dispatch = useAppDispatch()
    const cached = data?.length ?? 0 > 0
    useEffect(() => {
        setLoading(!cached);
        const id = route.params.albumId;
        dispatch(fetchAlbumMediaAsync(id))
            .then(() => {
                setLoading(false)
            })
            .catch(printException)
            .finally(() => setLoading(false));
    }, [route.params.albumId]);
    
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
                    layoutForType={(i, dim) => {
                        dim.width = rowItemWidth;
                        dim.height = 200;
                    }}
                    onVisibleIndicesChanged={onVisibleIndicesChanged}
                    onEndReached={onEndReached}
                    render={(media, i) => (
                        <MediaCard
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
