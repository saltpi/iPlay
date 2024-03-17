import { Api } from '@api/emby';
import {Season} from '@model/Season';
import {Image, ScrollView, StyleSheet, Text, View} from 'react-native';

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

export function SeasonCard({season}: {season: Season}) {
    console.log(season)
    return (
        <View style={style.root}>
            <Image
                style={style.cover}
                source={{
                    uri: Api.emby?.imageUrl?.(
                        season.Id,
                        season.ImageTags.Primary,
                        'Primary/0',
                    ),
                }}
            />
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
    return (
        <ScrollView horizontal={true} showsHorizontalScrollIndicator={false}>
            <View style={listStyle.root}>
                {seasons.map(season => (
                    <SeasonCard key={season.Id} season={season} />
                ))}
            </View>
        </ScrollView>
    );
}
