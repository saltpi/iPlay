import { imageUrl } from "@api/config";
import { Api } from "@api/emby";
import { ViewDetail } from "@model/View";
import { useEffect, useState } from "react";
import { Image, ScrollView, StyleProp, Text, View, ViewStyle } from "react-native";
import { Map } from "@model/Map";
import { Media } from "@model/Media";

const style: Map<string, StyleProp<ViewStyle>> = {
    root: {
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        margin: 10
    },
    album: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center"
    }
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
            console.log(medias)
            setMedias(medias)
        }
        getMedia()
    }, [albums])

    return (
        <View style={style.root}>
            {albums.map(album => {
                return (
                    <View key={album.Id} style={style.album}>
                        <Image
                            style={{ width: 160, height: 90 }}
                            source={{ uri: imageUrl(album.Id, album.Etag)}}
                            />
                        <Text>{album.Name}</Text>
                    </View>
                )
            })}
            {
                medias.map((media, i) => {
                    return (
                        <View key={i}>
                            <Text>{albums[i].Name}</Text>
                            {media?.map(m =>
                                <View key={m.Id}> 
                                    <Image style={{ width: 90, height: 150 }} source={{ uri: imageUrl(m.Id, m.Etag)}} />
                                    <Text>{m.Name}</Text>
                                </View>
                            )}
                        </View>
                    )
                })
            }
        </View>
    )
}