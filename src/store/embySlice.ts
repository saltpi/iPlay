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
import _, { Many } from 'lodash';
import { Media } from '@model/Media';
import { Map } from '@model/Map';
import { Actor } from '@model/Actor';
import { logger } from '@helper/log';
import { CollectionOptions } from '@api/view';

export enum SortType {
    NameAsc,
    NameDesc,
    AddedDateAsc,
    AddedDateDesc,
}

interface EmbyState {
    site: EmbySite|null;
    emby: Emby|null;
    sites?: EmbySite[];
    source: {
        albums?: ViewDetail[]
        latestMedias?: Media[][]
        actors?: Map<string, Actor>
        albumMedia?: Map<string, Media[]>
        resume?: Media[]
    }
    sortType?: SortType
}

const initialState: EmbyState = {
    site: null,
    emby: null,
    sites: [],
    source: {}
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

export const fetchEmbyActorAsync = createAppAsyncThunk<Actor|undefined, string>("emby/actor", async (id, config) => {
    const emby = await config.getState().emby.emby
    const data = await emby?.getActor?.(Number(id))
    const actor: Actor = {
        id: data?.Id ?? id,
        name: data?.Name ?? "",
        overview: data?.Overview ?? "",
        avatar: emby?.imageUrl?.(data?.Id ?? "", data?.ImageTags.Primary ?? "", "Primary")
    }
    return actor
})

export const fetchEmbyActorWorksAsync = createAppAsyncThunk<Media[]|undefined, string>("emby/actor/works", async (id, config) => {
    const emby = await config.getState().emby.emby
    const data = await emby?.getItem?.({
        PersonIds: id,
        IncludeItemTypes: "Movie,Series",
    })
    return data?.Items
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

export const fetchResumeMediaAsync = createAppAsyncThunk<Media[]|undefined, void>("emby/resume", async (_, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const medias = await emby?.getResume?.()
    return medias
})

export interface AlbumQueryParams {
    id: string,
    options?: CollectionOptions
}

export const fetchAlbumMediaAsync = createAppAsyncThunk("emby/album/media", async (params: string|AlbumQueryParams, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const id = typeof params === 'string' ? params : params.id
    const album = await emby?.getMedia?.(Number(id));
    const type = album?.CollectionType === 'tvshows' ? 'Series' : 'Movie';
    let startIdx = 0
    const data = await emby?.getCollection?.(Number(id), type, {
        StartIndex: startIdx,
        ...(typeof params === 'object' ? params.options : {})
    });
    const total = data?.TotalRecordCount;
    if (!total) return null
    const items: Media[] = [];
    items.push(...data.Items)
    startIdx += data.Items.length
    while (startIdx < total) {
        try {
            const data = await emby?.getCollection?.(Number(id), type, { StartIndex: startIdx, })
            if (!data) return null
            items.push(...data.Items)
            startIdx += data.Items.length
        } catch(e) {
            logger.error(e)
            return null
        }
    }
    return {
        id: album?.Id ?? id,
        items
    }
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

export type MarkFavoriteParams = {
    id: number,
    favorite: boolean
}

export const markFavoriteAsync = createAppAsyncThunk<boolean, MarkFavoriteParams>("emby/favorite", async ({id, favorite}, config) => {
    const state = await config.getState()
    const emby = state.emby.emby
    const data = await emby?.markFavorite?.(id, favorite);
    return data?.IsFavorite ?? false
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
        },
        updateAlbumSortType: (state, action: PayloadAction<SortType>) => {
            const sortType = action.payload
            state.sortType = sortType
            const albums = state.source.albumMedia
            if (!albums) return
            const keywords = sortType == SortType.NameAsc ? ["SortName"] : ["DateCreated", "SortName"]
            Object.entries(albums).forEach(([key, medias]) => {
                if (!medias) return
                const sortedMedias = _.sortBy(medias, keywords)
                albums[key] = sortedMedias
            })
            state.source.albumMedia = albums
        },
        updateToNextAlbumSortType: (state) => {
            const sortType = state.sortType
            const newSortType = ((sortType ?? 0) + 1)%4
            state.sortType = newSortType
            const albums = state.source.albumMedia
            if (!albums) return
            const keywords = newSortType == SortType.NameAsc ? ["SortName"] : ["DateCreated", "SortName"]
            const order: Many<'asc'|'desc'> = newSortType % 2 == 0 ? ['asc', 'asc'] : ['desc', 'desc']
            Object.entries(albums).forEach(([key, medias]) => {
                if (!medias) return
                const sortedMedias = _.orderBy(medias, keywords, order)
                albums[key] = sortedMedias
            })
            state.source.albumMedia = albums
        },
    },
    extraReducers: builder => {
        builder.addCase(loginToSiteAsync.pending, state => {

        })
        .addCase(loginToSiteAsync.fulfilled, (state, action) => {
            const site = action.payload
            if (!site) return
            state.site = site;
            state.emby = new Emby(site)
            const exist = state.sites?.some(old => old.id === site.id)
            if (exist) {
                for (let i = 0; i < state.sites!.length; i++) {
                    if (state.sites![i].id === site.id) {
                        state.sites![i] = site
                    }
                }
            } else {
                state.sites = [...(state.sites ?? []), site]
            }
        })
        .addCase(restoreSiteAsync.fulfilled, (state, action) => {
            state.site = action.payload;
            state.emby = action.payload ? new Emby(action.payload) : null
        })
        .addCase(switchToSiteAsync.fulfilled, (state, action) => {
            if (action.payload) state.site = action.payload
            state.emby = action.payload ? new Emby(action.payload) : null
            state.source = {}
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
            state.source.latestMedias = action.payload as Media[][]
        })
        .addCase(fetchEmbyActorAsync.fulfilled, (state, action) => {
            const actor = action.payload
            if (!actor) return
            if (!state.source.actors) {
                state.source.actors = {
                    [actor.id]: actor
                }
            } else if (state.source?.actors) {
                state.source.actors[actor.id] = actor
            }
        })
        .addCase(fetchAlbumMediaAsync.fulfilled, (state, action) => {
            const album = action.payload
            if (!album) return
            if (state.source.albumMedia) {
                state.source.albumMedia[album.id] = album.items
            } else {
                state.source.albumMedia = {
                    [album.id]: album.items
                }
            }
        })
        .addCase(fetchResumeMediaAsync.fulfilled, (state, action) => {
            const medias = action.payload
            if (!medias) return
            state.source.resume = medias
        })
    },
});

export const { 
    switchToSite, removeSite,
    updateCurrentEmbySite, 
    patchCurrentEmbySite,
    updateAlbumSortType,
    updateToNextAlbumSortType
} = slice.actions;

export const getActiveEmbySite = (state: RootState) => state.emby;
export const getImageUrl = (id: string | number, options: string) => 
    (state: RootState) => state.emby.emby?.imageUrl?.(id, options)

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