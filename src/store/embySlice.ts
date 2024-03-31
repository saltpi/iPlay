import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { listenerMiddleware } from './middleware/Listener';
import { RootState } from '.';
import { createAppAsyncThunk } from './type';
import { StorageHelper } from '@helper/store';
import { EmbySite } from '@model/EmbySite';
import { EmbyConfig } from '@helper/env';
import { Emby } from '@api/emby';
import { View, ViewDetail } from '@model/View';
import { PlaybackInfo } from '@model/PlaybackInfo';
import _ from 'lodash';
import { Media } from '@model/Media';

interface EmbyState {
    site: EmbySite|null;
    emby: Emby|null;
    sites?: EmbySite[];
    source?: {
        albums?: ViewDetail[]
        latestMedias?: Media[][]
    }
}

const initialState: EmbyState = {
    site: null,
    emby: null,
    sites: []
};

type Authentication = {
    endpoint?: EmbyConfig,
    username: string,
    password: string,
    callback?: {
        resolve?: () => void
        reject?: () => void
    }
}

export const restoreSiteAsync = createAppAsyncThunk<EmbySite|null, void>("emby/restore", async (_, config) => {
    const $site = await StorageHelper.get('@site');
    if (!$site) {
        console.log("no user or server")
        return null
    }
    try {
        const site = JSON.parse($site);
        return site
    } catch (e) {
        console.error(e);
    }
    return null
});

export const switchToSiteAsync = createAppAsyncThunk<EmbySite|undefined, string>("emby/switch", async (id, config) => {
    const state = config.getState().emby
    const site = state.sites?.filter(site => site.id === id)?.[0]
    if (site) {
        await StorageHelper.set("@site", JSON.stringify(site))
    }
    return site
});

export const loginToSiteAsync = createAppAsyncThunk<EmbySite|null, Authentication>("emby/site", async (user, config) => {
    const api = config.extra
    const data = await api.login(user.username, user.password, user.endpoint!)
    if (data) {
        const site: EmbySite = {
            id: data.ServerId,
            user: data, 
            server: user.endpoint!, 
            status: 'idle'
        }
        await StorageHelper.set("@site", JSON.stringify(site))
        return site
    }
    return null
})

export const fetchEmbyAlbumAsync = createAppAsyncThunk<View|undefined, void>("emby/view", async (_, config) => {
    const emby = await config.getState().emby.emby
    const data = emby?.getView?.()
    return data
})

export const fetchLatestMediaAsync = createAppAsyncThunk<(Media[]|undefined)[]|undefined, void>("emby/latest", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const albums = state.emby.source?.albums ?? []
    const medias = await Promise.all(
        albums.map(async album => {
            return await emby?.getLatestMedia?.(Number(album.Id));
        }),
    );
    return medias
})

export const helloAsync = createAsyncThunk<string, string, any>("emby/site", async (content, _config) => {
    return content
})

export const fetchPlaybackAsync = createAppAsyncThunk<PlaybackInfo|undefined, number>("emby/playback", async (vid, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const videoConfig = state.config.video
    const data = await emby?.getPlaybackInfo?.(vid, {
        MaxStreamingBitrate: videoConfig.MaxStreamingBitrate
    })
    return data
})

export const slice = createSlice({
    name: 'emby',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        updateCurrentEmbySite: (state, action: PayloadAction<EmbySite>) => {
            state.site = action.payload;
        },
        patchCurrentEmbySite: (state, action: PayloadAction<Partial<EmbySite>>) => {
            state.site = _.merge(state.site, action.payload)
            state.sites = state.sites?.map(site => {
                if (site.id === state.site?.id) {
                    return _.merge(site, action.payload)
                }
                return site
            })
        }, 
        switchToSite: (state, action: PayloadAction<string>) => {
            const id = action.payload
            const target = state.sites?.filter(site => site.id === id)?.[0]
            if (target) {
                state.site = target
            }
        },
        removeSite: (state, action: PayloadAction<string>) => {
            const id = action.payload
            state.sites = state.sites?.filter(site => site.id !== id)
        }
    },
    extraReducers: builder => {
        builder.addCase(loginToSiteAsync.pending, state => {
            if (state.site) state.site.status = 'loading';
        })
        .addCase(loginToSiteAsync.fulfilled, (state, action) => {
            const site = action.payload
            if (!site) return
            state.site = site;
            state.emby = new Emby(site)
            state.sites = [...(state.sites ?? []), site]
        })
        .addCase(restoreSiteAsync.fulfilled, (state, action) => {
            state.site = action.payload;
            state.emby = action.payload ? new Emby(action.payload) : null
        })
        .addCase(switchToSiteAsync.fulfilled, (state, action) => {
            if (action.payload) state.site = action.payload
            state.emby = action.payload ? new Emby(action.payload) : null
        })
        .addCase(fetchEmbyAlbumAsync.fulfilled, (state, action) => {
            if (state.source) {
                state.source.albums = action.payload?.Items ?? []
            } else {
                state.source = {
                    albums: action.payload?.Items ?? []
                }
            }
        })
        .addCase(fetchLatestMediaAsync.fulfilled, (state, action) => {
            if (state.source) {
                state.source.latestMedias = action.payload as Media[][]
            } else {
                state.source = {
                    latestMedias: action.payload as Media[][]
                }
            }
        })
    },
});

export const { switchToSite, removeSite, updateCurrentEmbySite, patchCurrentEmbySite } = slice.actions;
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

export default slice.reducer;