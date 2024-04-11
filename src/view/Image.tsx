import FastImage from 'react-native-fast-image';
import {Image as BaseImage} from 'react-native';
import {ComponentProps, useState} from 'react';
import { OSType, isOS } from '@helper/device';

export interface ImageProps extends ComponentProps<typeof FastImage> {
    fallbackImages?: string[];
}

export function MobileImage(props: ImageProps) {
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

export function WindowsImage(props: ImageProps) {
    const {source, style, ...rest} = props;
    return <BaseImage source={source as any}
            style={style as any}
            />;
}

export const Image = isOS(OSType.Windows) ? WindowsImage : MobileImage
export {BaseImage as BaseImage};
