import { imageUrl } from "@api/config";
import { Api } from "@api/emby";
import { ViewDetail } from "@model/View";
import { useEffect, useState } from "react";
import { Image, ScrollView, StyleProp, Text, View, ViewStyle } from "react-native";
import { Map } from "@model/Map";
import { Media } from "@model/Media";
import { useLinkTo, useNavigation } from "@react-navigation/native";
import { Navigation } from "@global";

const style: Map<string, StyleProp<ViewStyle>> = {
    root: {
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        margin: 10
    },
    albumList: {
        flexDirection: "row",
    },
    mediaCard: {
        margin: 10,
        overflow: "hidden",
        maxWidth: "33%",
        alignItems: "center",
    },
    album: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        borderRadius: 5,
        borderWidth: 1,
        borderColor: "transparent",
        marginBottom: 10,
    },
    albumItem: {
        flexDirection: "row",
        flexWrap: "wrap",
    }
}

export function MediaCard({media}: {media: Media}) {
    const navigation: Navigation = useNavigation();
    const onPress = (media: Media) => {
        navigation.navigate("movie", {
            title: media.Name,
            type: media.Type,
            movie: media
        });
    }
    return (
    <View onTouchEnd={() => onPress(media)} style={style.mediaCard} key={media.Id}> 
        <Image style={{width: 90, height: 150, borderRadius: 5 }} source={{ uri: imageUrl(media.Id, null)}} />
        <Text style={{maxWidth: 90, overflow: "hidden"}}>{media.Name}</Text>
        <Text>{media.ProductionYear}</Text>
    </View>
    )
}

export function AlbumItem({media, title}: {
    media?: Media[], 
    title: string}) {
    return (
        <View style={style.albumItem}>
            <Text>{title}</Text>
            <ScrollView horizontal={true}>
            {media?.map(m => <MediaCard key={m.Id} media={m} />)}
            </ScrollView>
        </View>
    )
}

export function AlbumCardList({albums}: {albums: ViewDetail[]}) {
    const navigation: Navigation = useNavigation();
    const onPress = (album: ViewDetail) => {
        navigation.navigate("album", {
            title: album.Name,
            albumId: album.Id,
            albumName: album.Name,
        })
    }
    return (
        <View style={style.albumList}>
            {albums.map(album => {
                return (
                    <View key={album.Id} 
                        style={style.album}
                        onTouchEnd={() => onPress(album)}
                        >
                        <Image
                            style={{ width: 160, height: 90 }}
                            source={{ uri: imageUrl(album.Id, album.Etag)}}
                            />
                        <Text>{album.Name}</Text>
                    </View>
                )
            })}
        </View>
    )
}

export function Album() {
    const [albums, setAlbums] = useState<ViewDetail[]>([])
    const [medias, setMedias] = useState<(Media[]|undefined)[]>([])
    useEffect(() => {
        Api.emby?.getView?.()
            .then(res => {
                setAlbums(res.Items)
            })
    }, [])

    useEffect(() => {
        const getMedia = async () => {
            const medias = await Promise.all(albums.map(async album => {
                return await Api.emby?.getLatestMedia?.(Number(album.Id))
            }))
            console.log(medias.length)
            setMedias(medias)
        }
        getMedia()
    }, [albums])

    return (
        <View style={style.root}>
            <AlbumCardList albums={albums} />
            {medias.map((media, i) => <AlbumItem key={albums[i].Id} media={media} title={albums[i].Name} />)}
        </View>
    )
}