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
import { Device } from '@helper/device';

export const style = StyleSheet.create({
    mediaCard: {
        margin: 10,
        overflow: 'hidden',
        alignItems: 'center',
    },
    mediaCardLine: {
        margin: 10,
        flexDirection: 'row',
        overflow: 'hidden',
        alignItems: 'center',
    },
    mediaCardText: {
        flex: 1,
        flexDirection: 'column',
        marginLeft: 10,
        alignItems: 'flex-start',
        justifyContent: "flex-start"
    },
    overview: {

    },
    inlineTitle: {
        marginTop: 10,
        marginBottom: 5,
        fontSize: 16,
        width: "100%",
        overflow: 'hidden',
        fontWeight: 'bold',
    },
    title: {
        maxWidth: 90, 
        overflow: 'hidden',
        fontWeight: 'bold',
    }
});

export function MediaCard({media, theme}: {media: Media, theme?: ThemeBasicStyle}) {
    const emby = useAppSelector(state => state.emby?.emby);
    const navigation: Navigation = useNavigation();
    const onPress = (media: Media) => {
        navigation.navigate('movie', {
            title: Device.isDesktop ? null : media.Name,
            type: media.Type,
            movie: media,
        } as any);
    };
    
    const postStyle = {
        width: media?.Type === "Episode" ? 160 : 90, 
        aspectRatio: media?.Type !== "Episode" ? 0.666 : 16/9, 
        borderRadius: media?.Type==="Episode" ? 7 : 5
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


export function MediaCardInLine({media, theme}: {media: Media, theme?: ThemeBasicStyle}) {
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
        width: media?.Type === "Episode" ? 160 : 90, 
        aspectRatio: media?.Type !== "Episode" ? 0.666 : 16/9, 
        borderRadius: media?.Type==="Episode" ? 7 : 5
    }
    return (
        <View style={style.mediaCardLine} key={media.Id}>
            <TouchableOpacity activeOpacity={1.0} onPress={() => onPress(media)}>
                <Image
                    style={postStyle}
                    source={{uri: emby?.imageUrl?.(media.Id, null)}}
                />
            </TouchableOpacity>
            <View style={style.mediaCardText}>
                <Text style={{...style.inlineTitle, ...theme}}
                    numberOfLines={1} 
                    ellipsizeMode="tail">
                    {media.Name}
                </Text>
                <Text style={{...style.overview, ...theme}}
                    numberOfLines={6} 
                    ellipsizeMode="tail">
                    {media.Overview}
                </Text>
                <Text style={theme}>{media.ProductionYear}</Text>
            </View>
        </View>
    );
}