import {Emby} from '@api/emby';
import {ViewDetail} from '@model/View';
import {
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native';
import { Image } from '@view/Image';
import {useNavigation} from '@react-navigation/native';
import {Navigation, ThemeBasicStyle} from '@global';
import { useAppSelector } from '@hook/store';

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