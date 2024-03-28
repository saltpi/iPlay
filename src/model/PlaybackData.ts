export interface PlaybackData {
    AudioStreamIndex?: number;
    CanSeek: boolean,
    EventName?: "timeupdate" | "pause",
    IsMuted: boolean,
    IsPaused: boolean,
    ItemId: string,
    MaxStreamingBitrate: number,
    MediaSourceId: string,
    PlaySessionId: string,
    PlayMethod: "DirectPlay" | "DirectStream" | "Transcode",
    PlaybackRate: number,
    PlaybackStartTimeTicks: number,
    PlaylistIndex: number,
    PlaylistLength: number,
    PositionTicks?: number,
    RepeatMode: "RepeatAll" | "RepeatOne" | "RepeatNone",
    SeekableRanges: [number, number][],
    SubtitleOffset: number,
    SubtitleStreamIndex: number,
    VolumeLevel: number
}

export const kPlaybackData: PlaybackData = {
    CanSeek: true,
    EventName: "timeupdate",
    IsMuted: false,
    IsPaused: false,
    ItemId: "",
    MaxStreamingBitrate: 0,
    MediaSourceId: "",
    PlaySessionId: "",
    PlayMethod: "DirectPlay",
    PlaybackRate: 0,
    PlaybackStartTimeTicks: 0,
    PlaylistIndex: -1,
    PlaylistLength: 0,
    PositionTicks: 0,
    RepeatMode: "RepeatAll",
    SeekableRanges: [],
    SubtitleOffset: 0,
    SubtitleStreamIndex: -1,
    VolumeLevel: 100
}

export const kPlayStopData: PlaybackData = {
    CanSeek: true,
    IsMuted: false,
    IsPaused: true,
    ItemId: "",
    MaxStreamingBitrate: 0,
    MediaSourceId: "",
    PlaySessionId: "",
    PlayMethod: "DirectPlay",
    PlaybackRate: 0,
    PlaybackStartTimeTicks: 0,
    PlaylistIndex: -1,
    PlaylistLength: 0,
    RepeatMode: "RepeatAll",
    SeekableRanges: [],
    SubtitleOffset: 0,
    SubtitleStreamIndex: -1,
    VolumeLevel: 100
}

export const kPlayStartData: PlaybackData = {
    ...kPlayStopData,
    PositionTicks: 0,
}

export const kSecond2TickScale = 10000000