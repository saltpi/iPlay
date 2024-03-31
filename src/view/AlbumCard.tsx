import { Text, View } from 'react-native';
import { Media } from '@model/Media';
import { ThemeBasicStyle } from '@global';
import { MediaCard } from './MediaCard';
import { ListView } from './ListView';
import { style } from './AlbumList';


export function AlbumCard({ media, title, theme }: { media?: Media[]; title: string; theme?: ThemeBasicStyle; }) {
    if (!media || !media.length) return null;
    return (
        <View style={style.albumItem}>
            <Text style={theme}>{title}</Text>
            <ListView items={media}
                isHorizontal={true}
                style={{ width: "100%", height: 200 }}
                layoutForType={(i, dim) => {
                    dim.width = 110;
                    dim.height = 200;
                }}
                render={m => <MediaCard key={m.Id}
                    media={m}
                    theme={theme} />} />
        </View>
    );
}
