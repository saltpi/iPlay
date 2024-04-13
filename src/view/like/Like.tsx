import {StyleSheet, TouchableOpacity, ViewStyle} from 'react-native';
import FavoriteIconOff from '@asset/favorite_off.svg';
import FavoriteIconOn from '@asset/favorite_on.svg';
import {Emby} from '@api/emby';
import {Toast} from '@helper/toast';
import {useSafeAreaInsets} from 'react-native-safe-area-context';
import {useState} from 'react';
import {printException} from '@helper/log';
import { useAppDispatch } from '@hook/store';
import { markFavoriteAsync } from '@store/embySlice';

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
    width?: number;
    isFavorite: boolean;
    style?: Partial<ViewStyle>;
}

export function Like({id, width = style.favorite.width, isFavorite, style: extStyle}: LikeProps) {
    const [favorite, setFavorite] = useState(isFavorite);
    const inset = useSafeAreaInsets();
    const dispatch = useAppDispatch();
    const markFavorite = (id: number, favorite: boolean) => {
        dispatch(markFavoriteAsync({id, favorite}))
            .then(res => {
                const isFavorite = res.payload
                if (typeof isFavorite !== 'boolean') return
                setFavorite(isFavorite);
                Toast.show({
                    type: 'success',
                    text1: isFavorite ? '已收藏' : '已取消收藏',
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
                    width={width}
                    style={style.favorite}
                />
            ) : (
                <FavoriteIconOn
                    width={width}
                    style={style.favorite}
                />
            )}
        </TouchableOpacity>
    );
}
