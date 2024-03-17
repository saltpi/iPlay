import {imageUrl} from '@api/config';
import {Api} from '@api/emby';
import {ViewDetail} from '@model/View';
import {useEffect, useState} from 'react';
import {
    Image,
    ScrollView,
    StyleSheet,
    Text,
    TouchableWithoutFeedback,
    View,
} from 'react-native';
import {Media} from '@model/Media';
import {useNavigation} from '@react-navigation/native';
import {Navigation} from '@global';
import { MediaCard } from './MediaCard';

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

export function AlbumCard({media, title}: {media?: Media[]; title: string}) {
    if (!media || !media.length) return null;
    return (
        <View style={style.albumItem}>
            <Text>{title}</Text>
            <ScrollView horizontal={true}
                showsHorizontalScrollIndicator={false}>
                {media?.map(m => (
                    <MediaCard key={m.Id} media={m} />
                ))}
            </ScrollView>
        </View>
    );
}

export function AlbumCardList({albums}: {albums: ViewDetail[]}) {
    const navigation: Navigation = useNavigation();
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
                    <TouchableWithoutFeedback key={album.Id} onPress={() => onPress(album)}>
                    <View key={album.Id} style={style.album}>
                        <Image style={style.albumImage}
                            source={{uri: imageUrl(album.Id, album.Etag)}}
                        />
                        <Text>{album.Name}</Text>
                    </View>
                    </TouchableWithoutFeedback>
                );
            })}
        </View>
        </ScrollView>
    );
}

export function AlbumWidget() {
    const [albums, setAlbums] = useState<ViewDetail[]>([]);
    const [medias, setMedias] = useState<(Media[] | undefined)[]>([]);
    useEffect(() => {
        Api.emby?.getView?.().then(res => {
            setAlbums(res.Items);
        });
    }, [Api.emby]);

    useEffect(() => {
        const getMedia = async () => {
            const medias = await Promise.all(
                albums.map(async album => {
                    return await Api.emby?.getLatestMedia?.(Number(album.Id));
                }),
            );
            console.log(medias.length);
            setMedias(medias);
        };
        getMedia();
    }, [albums]);

    return (
        <View style={style.root}>
            <AlbumCardList albums={albums} />
            {medias.map((media, i) => (
                <AlbumCard
                    key={albums[i].Id}
                    media={media}
                    title={albums[i].Name}
                />
            ))}
        </View>
    );
}
