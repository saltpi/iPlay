import { Navigation, ThemeBasicStyle } from '@global';
import { useAppSelector } from '@hook/store';
import {Season} from '@model/Season';
import { useNavigation } from '@react-navigation/native';
import {ScrollView, StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import { Image } from '@view/Image';
import { selectThemeBasicStyle } from '@store/themeSlice';

const style = StyleSheet.create({
    root: {
        width: 110,
        marginLeft: 3.5,
        marginRight: 3.5,
    },
    cover: {
        width: 110,
        aspectRatio: 2 / 3,
        borderRadius: 5,
        marginBottom: 2.5,
    },
    number: {
        position: 'absolute',
        top: 2.5,
        right: 2.5,
        backgroundColor: 'green',
        borderColor: 'transparent',
        color: 'white',
        fontWeight: '600',
        fontSize: 11.5,
        borderRadius: 10,
        borderWidth: 1,
        overflow: 'hidden',
        minWidth: 22,
        textAlign: 'center',
        padding: 1.5,
    },
    title: {
        textAlign: 'center',
    },
});

export interface SeasonCardProps {
    season: Season;
    theme?: ThemeBasicStyle;
    onPress?: (season: Season) => void;
}

export function SeasonCard({ season, theme, onPress }: SeasonCardProps) {
    const emby = useAppSelector(state => state.emby?.emby);
    return (
        <View style={style.root}>
            <TouchableOpacity activeOpacity={1.0} onPress={() => onPress?.(season)}>
            <Image
                style={style.cover}
                source={{
                    uri: emby?.imageUrl?.(
                        season.Id,
                        season.ImageTags.Primary,
                        'Primary/0',
                    ),
                }}
            />
            </TouchableOpacity>
            <Text style={{...style.number}}>{season.UserData.UnplayedItemCount}</Text>
            <Text style={{...style.title, ...theme}}>{season.Name}</Text>
        </View>
    );
}

const listStyle = StyleSheet.create({
    root: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        padding: 5,
    },
});

export function SeasonCardList({seasons}: {seasons: Season[]}) {
    const navigation: Navigation = useNavigation()
    const theme = useAppSelector(selectThemeBasicStyle)
    return (
        <ScrollView horizontal={true}
            contentContainerStyle={{minWidth: "100%"}}
            showsHorizontalScrollIndicator={false}>
            <View style={listStyle.root}>
                {seasons.map(season => (
                    <SeasonCard key={season.Id} 
                        theme={theme}
                        onPress={season => navigation.navigate('season', {title: season.Name, season})}
                        season={season} />
                ))}
            </View>
        </ScrollView>
    );
}
