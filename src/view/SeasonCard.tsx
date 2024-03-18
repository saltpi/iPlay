import { Api } from '@api/emby';
import { Navigation } from '@global';
import { useAppSelector } from '@hook/store';
import {Season} from '@model/Season';
import { useNavigation } from '@react-navigation/native';
import {ScrollView, StyleSheet, Text, TouchableOpacity, View} from 'react-native';
import { Image } from '@view/Image';

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
        fontSize: 12.5,
        borderRadius: 10,
        borderWidth: 1,
        overflow: 'hidden',
        minWidth: 20,
        textAlign: 'center',
        padding: 2.5,
    },
    title: {
        textAlign: 'center',
    },
});

export interface SeasonCardProps {
    season: Season;
    onPress?: (season: Season) => void;
}

export function SeasonCard({ season, onPress }: SeasonCardProps) {
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
            <Text style={style.number}>{season.UserData.UnplayedItemCount}</Text>
            <Text style={style.title}>{season.Name}</Text>
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
    return (
        <ScrollView horizontal={true} showsHorizontalScrollIndicator={false}>
            <View style={listStyle.root}>
                {seasons.map(season => (
                    <SeasonCard key={season.Id} 
                        onPress={season => navigation.navigate('season', {title: season.Name, season})}
                        season={season} />
                ))}
            </View>
        </ScrollView>
    );
}
