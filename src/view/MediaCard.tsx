import {
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from 'react-native';
import { Image } from '@view/Image';
import {Media} from '@model/Media';
import {useNavigation} from '@react-navigation/native';
import {Navigation, ThemeBasicStyle} from '@global';
import {Api} from '@api/emby';
import {useAppSelector} from '@hook/store';

export const style = StyleSheet.create({
    mediaCard: {
        margin: 10,
        overflow: 'hidden',
        alignItems: 'center',
    },
    title: {
        maxWidth: 90, 
        overflow: 'hidden'
    }
});

export function MediaCard({media, theme}: {media: Media, theme?: ThemeBasicStyle}) {
    const emby = useAppSelector(state => state.emby?.emby);
    const navigation: Navigation = useNavigation();
    const onPress = (media: Media) => {
        navigation.navigate('movie', {
            title: media.Name,
            type: media.Type,
            movie: media,
        });
    };
    const postStyle = {
        width: media.Type==="Episode" ? 160 : 90, 
        aspectRatio: media.PrimaryImageAspectRatio, 
        borderRadius: media.Type==="Episode" ? 7 : 5
    }
    return (
        <View style={style.mediaCard} key={media.Id}>
            <TouchableOpacity activeOpacity={1.0} onPress={() => onPress(media)}>
                <Image
                    style={postStyle}
                    source={{uri: emby?.imageUrl?.(media.Id, null)}}
                />
            </TouchableOpacity>
            <Text style={{...style.title, ...theme}}
                numberOfLines={1} 
                ellipsizeMode="tail">
                {media.Name}
            </Text>
            <Text style={theme}>{media.ProductionYear}</Text>
        </View>
    );
}
