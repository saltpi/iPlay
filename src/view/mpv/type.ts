export type VideoProps = {
    source: {
        uri: string;
        title: string;
    };
    onPlaybackStateChanged?: (state: {isPlaying: boolean}) => void;
}