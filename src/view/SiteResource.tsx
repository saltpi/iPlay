import { useEffect, useState } from 'react';
import { View } from 'react-native';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { fetchEmbyAlbumAsync, fetchLatestMediaAsync } from '@store/embySlice';
import { Spin } from './Spin';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { AlbumCard } from './AlbumCard';
import { style, AlbumCardList } from './AlbumList';


export function SiteResource() {
    const albums = useAppSelector(state => state.emby.source?.albums);
    const medias = useAppSelector(state => state.emby.source?.latestMedias);
    const [loading, setLoading] = useState(false);
    const dispatch = useAppDispatch();
    const theme = useAppSelector(selectThemeBasicStyle);
    const site = useAppSelector(state => state.emby.site);

    useEffect(() => {
        dispatch(fetchEmbyAlbumAsync());
    }, [site]);

    useEffect(() => {
        const getMedia = async () => {
            setLoading(true);
            await dispatch(fetchLatestMediaAsync());
            setTimeout(() => {
                setLoading(false);
            }, 1000);
        };
        getMedia();
    }, [albums]);

    return (
        <View style={{ ...style.root, ...theme }}>
            {loading ? <Spin color={theme.color} /> : null}
            {albums ? <AlbumCardList albums={albums} theme={theme} /> : null}
            {medias?.map((media, i) => (
                <AlbumCard
                    key={albums?.[i]?.Id ?? i}
                    media={media}
                    title={albums?.[i]?.Name ?? ""}
                    theme={theme} />
            ))}
        </View>
    );
}
