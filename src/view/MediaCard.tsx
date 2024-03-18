import {
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native';
import { Image } from '@view/Image';
import {Media} from '@model/Media';
import {useNavigation} from '@react-navigation/native';
import {Navigation} from '@global';
import {Api} from '@api/emby';
import {useAppSelector} from '@hook/store';

export const style = StyleSheet.create({
    mediaCard: {
        margin: 10,
        overflow: 'hidden',
        maxWidth: '33%',
        alignItems: 'center',
    },
    title: {
        maxWidth: 90, 
        overflow: 'hidden'
    }
});

export function MediaCard({media}: {media: Media}) {
    const emby = useAppSelector(state => state.emby?.emby);
    const navigation: Navigation = useNavigation();
    const onPress = (media: Media) => {
        navigation.navigate('movie', {
            title: media.Name,
            type: media.Type,
            movie: media,
        });
    };
    return (
        <View style={style.mediaCard} key={media.Id}>
            <TouchableOpacity activeOpacity={1.0} onPress={() => onPress(media)}>
                <Image
                    style={{width: 90, aspectRatio: 4.6 / 7, borderRadius: 5}}
                    source={{uri: emby?.imageUrl?.(media.Id, null)}}
                />
            </TouchableOpacity>
            <Text style={style.title}
                numberOfLines={1} 
                ellipsizeMode="tail">
                {media.Name}
            </Text>
            <Text>{media.ProductionYear}</Text>
        </View>
    );
}
