import FastImage from 'react-native-fast-image';
import {Image as BaseImage} from 'react-native';
import {ComponentProps, useState} from 'react';

export interface ImageProps extends ComponentProps<typeof FastImage> {
    fallbackImages?: string[];
}

export function Image(props: ImageProps) {
    const [imageTryCount, setImageTryCount] = useState(0);
    const {source: origin, fallbackImages, ...rest} = props;
    const source =
        typeof origin != 'number' &&
        imageTryCount > 0 &&
        imageTryCount <= (fallbackImages?.length || 0)
            ? {uri: fallbackImages?.[imageTryCount - 1]}
            : origin;

    return (
        <FastImage
            {...rest}
            source={source}
            onError={() => setImageTryCount(i => i + 1)}
        />
    );
}

export {BaseImage as BaseImage};
