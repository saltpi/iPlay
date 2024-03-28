import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { listenerMiddleware } from './middleware/Listener';
import { RootState } from '.';
import { createAppAsyncThunk } from './type';
import { StorageHelper } from '@helper/store';
import { EmbySite } from '@model/EmbySite';
import { EmbyConfig } from '@helper/env';
import { Emby } from '@api/emby';
import { View } from '@model/View';
import { kPlaybackData, kPlayStartData } from '@model/PlaybackData';

interface PlayerState {
    source: "emby" | "local";
    status?: "playing" | "paused" | "stopped";

    // for type emby
    mediaName?: string;
    mediaId?: string;
    mediaPlot?: string;
    mediaType?: string;
    mediaEvent?: "start" | "timeupdate" | "pause" | "stop";
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
}

const initialState: PlayerState = {
    source: "local",
};


export const trackPlayAsync = createAppAsyncThunk<View|undefined, void>("player/track", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    const data = emby?.trackPlay?.({
        ...kPlaybackData,
        ItemId: player.mediaId,
        PlaySessionId: player.sessionId,
        PlaybackStartTimeTicks: player.startTime,
        PositionTicks: player.position,
    })
    return data
})

export const startPlayAsync = createAppAsyncThunk<View|undefined, void>("player/start", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    const data = await emby?.startPlay?.({
        ...kPlayStartData,
        ItemId: player.mediaId,
        PlaySessionId: player.sessionId,
        PlaybackStartTimeTicks: player.startTime,
        PositionTicks: 0,
    })
    return data
})

export const stopPlayAsync = createAppAsyncThunk<View|undefined, void>("player/stop", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const player = state.player
    const data = await emby?.stopPlay?.({
        ...kPlayStartData,
        ItemId: player.mediaId,
        PlaySessionId: player.sessionId,
        PlaybackStartTimeTicks: player.startTime,
        PositionTicks: 0,
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