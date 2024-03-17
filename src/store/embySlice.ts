import { EmbyConfig } from '@api/config';
import { User } from '@model/User';
import { createAsyncThunk, createSlice, PayloadAction, ThunkAction } from '@reduxjs/toolkit';
import { listenerMiddleware } from './middleware/Listener';
import { RootState } from '.';
import { createAppAsyncThunk } from './type';
import { Emby } from '@api/emby';
import { StorageHelper } from '@helper/store';

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

export const loginToSiteAsync = createAppAsyncThunk<User, Authentication>("emby/site", async (user, config) => {
    const api = config.extra
    const state = config.getState()
    const data = await api.login(user.username, user.password, state.emby.site?.server)
    if (data) {
        await StorageHelper.set("@user", JSON.stringify(data))
        api.emby = new Emby(data)
        if (state.emby.site) {
            state.emby.site.user = data
        }
    }
    return data
})

export const helloAsync = createAsyncThunk<string, string, any>("emby/site", async (content, _config) => {
    return content
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
        .addCase(loginToSiteAsync.fulfilled, (state, action) => {
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
    effect: async (data, _api) => {
        data.meta.arg.callback?.resolve?.()
    }
})

listenerMiddleware.startListening({
    actionCreator: loginToSiteAsync.rejected,
    effect: async (data, _api) => {
        data.meta.arg.callback?.reject?.()
    }
})

export default EmbySlice.reducer;