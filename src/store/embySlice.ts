import { EmbyConfig } from '@api/config';
import { Emby } from '@api/emby';
import { User } from '@model/User';
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '@store/store';
import { createAppAsyncThunk } from './type';
import { listenerMiddleware } from './middleware/Listener';

interface EmbySite {
    server: EmbyConfig;
    user: User;
    status: 'idle' | 'loading' | 'failed';
}

interface EmbyState {
    site: EmbySite|null;
}

const initialState: EmbyState = {
    site: null
};

type Authentication = {
    username: string,
    password: string,
    callback?: {
        resolve?: () => void
        reject?: () => void
    }
}

export const getSiteInfo = createAppAsyncThunk("site/info", async (id: number, api) => {
    const response = await fetch("/manifest.json")
    const data = await response.json()
    return data
})

export const loginToSiteAsync = createAppAsyncThunk("emby/site", async (user: Authentication, config) => {
    const api = config.extra
    const state = config.getState()
    const data = await api.login(user.username, user.password, state.emby.site?.server)
    if (data) {
        api.emby = new Emby(data)
    }
    return data
})

export const EmbySlice = createSlice({
    name: 'emby',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        updateCurrentEmbySite: (state, action: PayloadAction<EmbySite>) => {
            state.site = action.payload;
        },
    },
    extraReducers: builder => {
        builder.addCase(loginToSiteAsync.pending, state => {
            if (state.site) state.site.status = 'loading';
        })
        builder.addCase(loginToSiteAsync.fulfilled, (state, action) => {
            if (!state.site) return
            state.site.status = 'idle';
            state.site.user = action.payload;
        });
    },
});

export const { updateCurrentEmbySite } = EmbySlice.actions;
export const getActiveEmbySite = (state: RootState) => state.emby;


listenerMiddleware.startListening({
    actionCreator: loginToSiteAsync.fulfilled,
    effect: async (data, api) => {
        data.meta.arg.callback?.resolve?.()
    }
})

listenerMiddleware.startListening({
    actionCreator: loginToSiteAsync.rejected,
    effect: async (data, api) => {
        data.meta.arg.callback?.reject?.()
    }
})

export default EmbySlice.reducer;