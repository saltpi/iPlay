export interface PlaybackData {
    AudioStreamIndex?: number;
    BufferedRanges?: [number, number][]
    CanSeek: boolean,
    EventName?: "TimeUpdate" | "Pause",
    NowPlayingQueue?: {
        Id: string,
        PlaylistItemId: string,
    }[]
    IsMuted: boolean,
    IsPaused: boolean,
    ItemId: string,
    MaxStreamingBitrate?: number,
    MediaSourceId: string,
    PlaySessionId: string,
    PlayMethod: "DirectPlay" | "DirectStream" | "Transcode",
    PlaybackRate?: number,
    PlaybackStartTimeTicks?: number,
    PlaylistIndex: number,
    PlaylistLength: number,
    PositionTicks?: number,
    RepeatMode: "RepeatAll" | "RepeatOne" | "RepeatNone",
    SeekableRanges?: {start: number, end: number}[],
    SubtitleOffset?: number,
    SubtitleStreamIndex?: number,
    VolumeLevel?: number
}

export const kPlaybackData: PlaybackData = {
    MediaSourceId: "",
    IsMuted: false,
    IsPaused: false,
    RepeatMode: "RepeatNone",
    PositionTicks: 0,
    NowPlayingQueue: [],
    PlayMethod: "DirectStream",
    PlaySessionId: "",
    CanSeek: true,
    ItemId: "-1",
    EventName: "TimeUpdate",
    PlaylistIndex: 0,
    PlaylistLength: 1,
}

export const kPlayStopData: PlaybackData = {
    AudioStreamIndex: 1,
    CanSeek: true,
    IsMuted: false,
    IsPaused: true,
    ItemId: "",
    MaxStreamingBitrate: 7000000,
    MediaSourceId: "",
    PlaySessionId: "",
    PlayMethod: "DirectStream",
    PlaybackRate: 1,
    PlaybackStartTimeTicks: 0,
    PlaylistIndex: 0,
    PlaylistLength: 1,
    RepeatMode: "RepeatAll",
    SeekableRanges: [],
    SubtitleOffset: 0,
    SubtitleStreamIndex: 0,
    VolumeLevel: 100
}

export const kPlayStartData: PlaybackData = {
    ...kPlayStopData,
    PositionTicks: 0,
}

export const kSecond2TickScale = 10000000