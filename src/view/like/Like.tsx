import {StyleSheet, TouchableOpacity, ViewStyle} from 'react-native';
import FavoriteIconOff from '@asset/favorite_off.svg';
import FavoriteIconOn from '@asset/favorite_on.svg';
import {Emby} from '@api/emby';
import {Toast} from '@helper/toast';
import {useSafeAreaInsets} from 'react-native-safe-area-context';
import {useState} from 'react';
import {printException} from '@helper/log';

const style = StyleSheet.create({
    icon: {
        width: 28,
    },
    favorite: {
        width: 28,
        height: 28,
        flexShrink: 0,
        flexGrow: 0,
    },
});

export interface LikeProps {
    id: number;
    isFavorite: boolean;
    emby?: Emby|null;
    style?: Partial<ViewStyle>;
}

export function Like({id, isFavorite, emby, style: extStyle}: LikeProps) {
    const [favorite, setFavorite] = useState(isFavorite);
    const inset = useSafeAreaInsets();
    const markFavorite = (id: number, favorite: boolean) => {
        emby?.markFavorite?.(id, favorite)
            .then(data => {
                setFavorite(data.IsFavorite);
                Toast.show({
                    type: 'success',
                    text1: data.IsFavorite ? '已收藏' : '已取消收藏',
                    topOffset: inset.top + 2.5,
                });
            })
            .catch(printException);
    };
    return (
        <TouchableOpacity
            activeOpacity={1.0}
            style={{...style.icon, ...extStyle}}
            onPress={() => markFavorite(Number(id ?? 0), !favorite)}>
            {favorite ? (
                <FavoriteIconOff
                    width={style.favorite.width}
                    style={style.favorite}
                />
            ) : (
                <FavoriteIconOn
                    width={style.favorite.width}
                    style={style.favorite}
                />
            )}
        </TouchableOpacity>
    );
}
