import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { createAppAsyncThunk } from './type';
import { kPlaybackData, kPlayStartData, kSecond2TickScale } from '@model/PlaybackData';

interface PlayerState {
    source: "emby" | "local";
    status?: undefined | "start" | "playing" | "paused" | "stopped";

    // for type emby
    mediaName?: string;
    mediaId?: string;
    mediaSourceId?: string;
    mediaPlot?: string;
    mediaType?: string;
    mediaEvent?: "Start" | "TimeUpdate" | "Pause" | "Stop";
    mediaPoster?: string;
    seasonId?: string;
    sessionId?: string;
    startTime?: number;
    duration?: number;
    position?: number;
    isPaused?: boolean;
    isMuted?: boolean;
    volumeLevel?: number;
    playbackRate?: number;
    subtitleOffset?: number;
    subtitleStreamIndex?: number;
    audioStreamIndex?: number;
    maxStreamingBitrate?: number;


    // UI
    fontFamily?: string
}

const initialState: PlayerState = {
    source: "local",
};

function second2Tick(second: number = 0) {
    return Math.round(second * kSecond2TickScale)
}

export type TrackPlayOption = {
    isPause?: boolean
}

export const trackPlayAsync = createAppAsyncThunk("player/track", async ({isPause = false}: TrackPlayOption, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    console.log(`track ${isPause ? "pause" : "play"}`, player.mediaId, player.sessionId, player.startTime, player.position)
    const data = emby?.trackPlay?.({
        ...kPlaybackData,
        IsPaused: isPause,
        EventName: isPause ? "Pause" : "TimeUpdate",
        ItemId: player.mediaId,
        MediaSourceId: player.mediaSourceId,
        PlaySessionId: player.sessionId,
        PositionTicks: second2Tick(player.position),
        NowPlayingQueue: [
            {Id: player.mediaId ?? "", PlaylistItemId: "playlistItem0"}
        ]
    })
    return data
})

export const startPlayAsync = createAppAsyncThunk("player/start", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    console.log("start play", player.mediaId, player.sessionId, player.startTime, player.position)
    const data = await emby?.startPlay?.({
        ...kPlayStartData,
        ItemId: player.mediaId,
        MediaSourceId: player.mediaSourceId,
        PlaySessionId: player.sessionId,
        SeekableRanges: [{start: 0, end: second2Tick(player.duration)}],
        PositionTicks: second2Tick(player.position),
        NowPlayingQueue: [
            {Id: player.mediaId ?? "", PlaylistItemId: "playlistItem0"}
        ]
    })
    return data
})

export const stopPlayAsync = createAppAsyncThunk("player/stop", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    if (!player.mediaId) return
    console.log("stop play", player.mediaId, player.sessionId, player.startTime, player.position)
    const data = await emby?.stopPlay?.({
        ...kPlayStartData,
        ItemId: player.mediaId,
        MediaSourceId: player.mediaSourceId,
        PlaySessionId: player.sessionId,
        SeekableRanges: [{start: 0, end: second2Tick(player.duration)}],
        // PlaybackStartTimeTicks: second2Tick(player.startTime),
        PositionTicks: second2Tick(player.position),
    })
    return data
})

export const slice = createSlice({
    name: 'player',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        updatePlayerState: (state, action: PayloadAction<Partial<PlayerState>>) => {
            return {
                ...state,
                ...action.payload
            }
        },
    },
    extraReducers: builder => {
        builder
        .addCase(startPlayAsync.fulfilled, (state, action) => {

        })
        .addCase(trackPlayAsync.fulfilled, (state, action) => {

        })
        .addCase(stopPlayAsync.fulfilled, (state, action) => {
                
        })
    },
});

export const { updatePlayerState } = slice.actions;

export default slice.reducer;