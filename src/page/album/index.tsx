import {Emby} from '@api/emby';
import {PropsWithNavigation} from '@global';
import {printException} from '@helper/log';
import {useAppSelector} from '@hook/store';
import {Media} from '@model/Media';
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

async function getAlbum(emby: Emby, id: number, startIdx: number = 0) {
    const album = await emby?.getMedia?.(id);
    const type = album?.CollectionType === 'tvshows' ? 'Series' : 'Movie';
    const data = await emby?.getCollection?.(id, type, {
        StartIndex: startIdx,
    });
    return {album, data};
}

export function Page({route, navigation}: PropsWithNavigation<'album'>) {
    const emby = useAppSelector(state => state.emby?.emby);
    const [data, setData] = useState<Media[]>();
    const [totalCount, setTotalCount] = useState(0);
    const [loading, setLoading] = useState(true);
    const theme = useAppSelector(selectThemeBasicStyle);
    const pageStyle = useAppSelector(selectThemedPageStyle);
    useEffect(() => {
        if (!emby) return;
        setLoading(true);
        getAlbum(emby, Number(route.params.albumId), 0)
            .then(res => {
                const total = res.data?.TotalRecordCount;
                if (!total) return;
                const newItems = res.data?.Items ?? [];
                setTotalCount(total ?? 0);
                const items: Media[] = [];
                for (let i = 0; i < total; i++) {
                    items.push({
                        Id: i.toString(),
                        Name: 'Loading',
                    } as any);
                }
                for (let i = 0; i < newItems.length; i++) {
                    items[i] = newItems[i];
                }
                setData(data => items);
            })
            .catch(printException)
            .finally(() => setLoading(false));
    }, [route.params.albumId, emby]);

    useEffect(() => {
        if (!emby) return;
        for (let i = 0; i < totalCount; i += 50) {
            getAlbum(emby, Number(route.params.albumId), i)
                .then(res => {
                    const newItems = res.data?.Items;
                    if (!newItems) return;
                    setData(data => {
                        const items = data ?? [];
                        const start = i,
                            end = i + newItems.length;
                        for (let j = start; j < end; j++) {
                            items[j] = newItems[j - start];
                        }
                        return items;
                    });
                })
                .catch(printException);
        }
    }, [totalCount]);

    const onVisibleIndicesChanged = (indices: number[]) => {
        const min = indices?.[0];
        const max = indices?.[indices.length - 1];
    };

    const onEndReached = () => {};

    const rowItemWidth =
        (kFullScreenStyle.width - 20) /
        Math.floor((kFullScreenStyle.width - 20) / 120);

    return (
        <View style={{...style.root, ...pageStyle}}>
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
