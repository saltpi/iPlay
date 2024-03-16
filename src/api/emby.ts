import { User } from "@model/User";
import { getPlaybackInfo } from "./play";
import { getCollection, getEpisodes, getLatestMedia, getMedia, getRecommendations, getResume, getSeasons, getView, lookupItem, searchRecommend } from "./view";
import { getPublicInfo } from "./info";
import { login } from "./login";

export class Emby {
    private _user?: User;
    get user() {
        if (!this._user) throw Error("user can't be null")
        return this._user;
    }
    set user(user: User) {
        this._user = user
        this.bind()
    }

    constructor(user?: User) {
        this._user = user
        this.bind()
    }

    bind() {
        this.getMedia = getMedia.bind(this, this.user)
        this.getPlaybackInfo = getPlaybackInfo?.bind(this, this.user)
        this.getView = getView.bind(this, this.user)
        this.getLatestMedia = getLatestMedia.bind(this, this.user)
        this.getCollection = getCollection.bind(this, this.user)
        this.getResume = getResume.bind(this, this.user)
        this.getRecommendations = getRecommendations.bind(this, this.user)
        this.getEpisodes = getEpisodes.bind(this, this.user)
        this.getSeasons = getSeasons.bind(this, this.user)
        this.getItemWithName = lookupItem.bind(this, this.user)
        this.searchRecommend = searchRecommend.bind(this, this.user)
    }

    public getPlaybackInfo = this._user ? getPlaybackInfo.bind(this, this.user) : null
    public getMedia = this._user ? getMedia.bind(this, this.user) : null
    public getView = this._user ? getView.bind(this, this.user) : null
    public getLatestMedia = this._user ? getLatestMedia.bind(this, this.user) : null
    public getPublicInfo = getPublicInfo
    public getCollection = this._user ? getCollection.bind(this, this.user) : null
    public getResume = this._user ? getResume.bind(this, this.user) : null
    public getRecommendations = this._user ? getRecommendations.bind(this, this.user) : null
    public getEpisodes = this._user ? getEpisodes.bind(this, this.user) : null
    public getSeasons = this._user ? getSeasons.bind(this, this.user) : null
    public getItemWithName = this._user ? lookupItem.bind(this, this.user) : null
    public searchRecommend = this._user ? searchRecommend.bind(this, this.user) : null
}

export const Api = {
    emby: null as Emby|null,
    login: login
};