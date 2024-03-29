export interface PlaybackData {
    AudioStreamIndex?: number;
    BufferedRanges: [number, number][]
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
    SeekableRanges: {start: number, end: number}[],
    SubtitleOffset: number,
    SubtitleStreamIndex: number,
    VolumeLevel: number
}

export const kPlaybackData: PlaybackData = {
    VolumeLevel: 100,
    IsMuted: false,
    IsPaused: true,
    RepeatMode: "RepeatNone",
    SubtitleOffset: 0,
    PlaybackRate: 1,
    MaxStreamingBitrate: 7000000,
    PositionTicks: 0,
    SubtitleStreamIndex: 3,
    AudioStreamIndex: 1,
    BufferedRanges: [],
    SeekableRanges: [
        {
            "start": 0,
            "end": -1 
        }
    ],
    PlayMethod: "DirectPlay",
    PlaySessionId: "2eb21e210d9445e1b6a2fced0d506adb",
    MediaSourceId: "c3a29bc633cec7d531d3eb34b672b8e6",
    CanSeek: true,
    ItemId: "-1",
    EventName: "timeupdate",
    PlaylistIndex: 0,
    PlaylistLength: 1,
    PlaybackStartTimeTicks: 0
}

export const kPlayStopData: PlaybackData = {
    AudioStreamIndex: 1,
    BufferedRanges: [],
    CanSeek: true,
    IsMuted: false,
    IsPaused: true,
    ItemId: "",
    MaxStreamingBitrate: 7000000,
    MediaSourceId: "",
    PlaySessionId: "",
    PlayMethod: "DirectPlay",
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