import {Api, Emby} from '@api/emby';
import {ViewDetail} from '@model/View';
import {useEffect, useState} from 'react';
import {
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native';
import { Image } from '@view/Image';
import {Media} from '@model/Media';
import {useNavigation} from '@react-navigation/native';
import {Navigation, ThemeBasicStyle} from '@global';
import { MediaCard } from './MediaCard';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { EmbySite } from '@model/EmbySite';
import { fetchEmbyAlbumAsync } from '@store/embySlice';
import { Spin } from './Spin';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { ListBaseView, ListView } from './ListView';

export const style = StyleSheet.create({
    root: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        margin: 10,
    },
    albumList: {
        flexDirection: 'row',
    },
    album: {
        display: 'flex',
        justifyContent: 'center',
        margin: 5,
        alignItems: 'center',
        borderRadius: 5,
        borderWidth: 1,
        borderColor: 'transparent',
        marginBottom: 10,
    },
    albumItem: {
        flexDirection: 'row',
        flexWrap: 'wrap',
    },
    albumImage: {
        width: 160, 
        aspectRatio: 16/9,
        borderRadius: 5,
        marginBottom: 2.5
    }
});

export function AlbumCard({media, title, theme}: {media?: Media[]; title: string, theme?: ThemeBasicStyle}) {
    if (!media || !media.length) return null;
    return (
        <View style={style.albumItem}>
            <Text style={theme}>{title}</Text>
            <ListView items={media} 
                isHorizontal={true}
                style={{width: "100%", height: 200}}
                layoutForType={(i, dim) => {
                    dim.width = 120;
                    dim.height = 200;
                }}
                render={m => 
                <MediaCard key={m.Id} 
                    media={m} 
                    theme={theme} />
                }
            />
        </View>
    );
}

export function AlbumCardList({albums, theme}: {albums: ViewDetail[], theme?: ThemeBasicStyle}) {
    const navigation: Navigation = useNavigation();
    const emby = useAppSelector(state => state.emby.emby)
    const onPress = (album: ViewDetail) => {
        navigation.navigate('album', {
            title: album.Name,
            albumId: album.Id,
            albumName: album.Name,
        });
    };
    return (
        <ScrollView horizontal={true} showsHorizontalScrollIndicator={false}>
        <View style={style.albumList}>
            {albums.map(album => {
                return (
                    <TouchableOpacity activeOpacity={1.0} key={album.Id} onPress={() => onPress(album)}>
                    <View key={album.Id} style={style.album}>
                        <Image style={style.albumImage}
                            source={{uri: emby?.imageUrl?.(album.Id, album.Etag)}}
                        />
                        <Text style={theme}>{album.Name}</Text>
                    </View>
                    </TouchableOpacity>
                );
            })}
        </View>
        </ScrollView>
    );
}

export interface SiteResourceProps {
    etag: string;
}

export function SiteResource({etag}: SiteResourceProps) {
    const [albums, setAlbums] = useState<ViewDetail[]>([]);
    const [medias, setMedias] = useState<(Media[] | undefined)[]>([]);
    const [loading, setLoading] = useState(false);
    const dispatch = useAppDispatch()
    const emby = useAppSelector(state => state.emby.emby)
    const theme = useAppSelector(selectThemeBasicStyle)

    const getMediaContent = async (emby: Emby) => {
        dispatch(fetchEmbyAlbumAsync()).then(data => {
            if (typeof data.payload === "string") return
            setAlbums(data.payload?.Items || [])
        })
    }
    useEffect(() => {
        if (!emby) return
        getMediaContent(emby)
    }, [emby, etag]);

    useEffect(() => {
        const getMedia = async () => {
            setLoading(true);
            const medias = await Promise.all(
                albums.map(async album => {
                    return await emby?.getLatestMedia?.(Number(album.Id));
                }),
            );
            setMedias(medias);
            setTimeout(() => {
                setLoading(false);
            }, 1000);
        };
        getMedia();
    }, [albums, emby]);

    return (
        <View style={{...style.root, ...theme}}>
            {loading ?  <Spin color={theme.color} /> : null}
            <AlbumCardList albums={albums} theme={theme} />
            {medias.map((media, i) => (
                <AlbumCard
                    key={albums[i]?.Id ?? i}
                    media={media}
                    title={albums[i]?.Name}
                    theme={theme}
                />
            ))}
        </View>
    );
}
