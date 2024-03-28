import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { listenerMiddleware } from './middleware/Listener';
import { RootState } from '.';
import { createAppAsyncThunk } from './type';
import { StorageHelper } from '@helper/store';
import { EmbySite } from '@model/EmbySite';
import { EmbyConfig } from '@helper/env';
import { Emby } from '@api/emby';
import { View } from '@model/View';

interface PlayerState {
    type: "emby" | "local";

    // for type emby
    mediaId?: string;
    sessionId?: string;
    startTime?: number;
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
    type: "local",
};


export const trackPlayStateAsync = createAppAsyncThunk<View|undefined, void>("player/track", async (_, config) => {
    const emby = await config.getState().emby.emby
    const data = emby?.getView?.()
    return data
})

export const slice = createSlice({
    name: 'player',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        updateCurrentEmbySite: (state, action: PayloadAction<EmbySite>) => {
        },
    },
    extraReducers: builder => {
        builder
        .addCase(trackPlayStateAsync.fulfilled, (state, action) => {

        })
    },
});

export const { updateCurrentEmbySite } = slice.actions;
export const getActiveEmbySite = (state: RootState) => state.emby;

export default slice.reducer;