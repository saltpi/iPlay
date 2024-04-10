import {PropsWithNavigation} from '@global';
import {logger, printException} from '@helper/log';
import {useAppDispatch, useAppSelector} from '@hook/store';
import { fetchAlbumMediaAsync } from '@store/embySlice';
import {LayoutType, selectThemeBasicStyle, selectThemedPageStyle} from '@store/themeSlice';
import {ListView, kFullScreenStyle} from '@view/ListView';
import {MediaCard, MediaCardInLine} from '@view/MediaCard';
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


export function Page({ navigation, route}: PropsWithNavigation<'album'>) {
    const data = useAppSelector(state => state.emby?.source?.albumMedia?.[route.params.albumId]);
    const [loading, setLoading] = useState(true);
    const theme = useAppSelector(selectThemeBasicStyle);
    const pageStyle = useAppSelector(selectThemedPageStyle);
    const dispatch = useAppDispatch()
    const layoutType = useAppSelector(state => state.theme.albumLayoutType);
    const cached = data?.length ?? 0 > 0

    useEffect(() => {
        setLoading(!cached);
        logger.info("fetch album media", route.params.albumId)
        const id = route.params.albumId;
        const query = {
            id, 
            options: {
                SortBy: "DateLastContentAdded,SortName"
            }
        }
        dispatch(fetchAlbumMediaAsync(query))
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
                    typeForIndex={i => layoutType ?? LayoutType.Card}
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
