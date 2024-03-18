import { getPlaybackInfo } from "./play";
import { getCollection, getEpisodes, getLatestMedia, getMedia, getRecommendations, getResume, getSeasons, getView, lookupItem, searchRecommend } from "./view";
import { getPublicInfo } from "./info";
import { login } from "./login";
import { EmbySite } from "@model/EmbySite";
import { imageUrl, playUrl } from "./config";

export class Emby {
    private _site?: EmbySite;
    get site() {
        if (!this._site) throw Error("site can't be null")
        return this._site;
    }
    set site(site: EmbySite) {
        this._site = site
        this.bind()
    }

    constructor(site?: EmbySite) {
        this._site = site
        this.bind()
    }

    bind() {
        this.getMedia = getMedia.bind(this, this.site)
        this.getPlaybackInfo = getPlaybackInfo?.bind(this, this.site)
        this.getView = getView.bind(this, this.site)
        this.getLatestMedia = getLatestMedia.bind(this, this.site)
        this.getCollection = getCollection.bind(this, this.site)
        this.getResume = getResume.bind(this, this.site)
        this.getRecommendations = getRecommendations.bind(this, this.site)
        this.getEpisodes = getEpisodes.bind(this, this.site)
        this.getSeasons = getSeasons.bind(this, this.site)
        this.getItemWithName = lookupItem.bind(this, this.site)
        this.searchRecommend = searchRecommend.bind(this, this.site)
        this.getPublicInfo = getPublicInfo.bind(this, this.site)
        this.imageUrl = imageUrl.bind(this, this.site)
        this.videoUrl = playUrl.bind(this, this.site)
    }

    public getPlaybackInfo = this._site ? getPlaybackInfo.bind(this, this.site) : null
    public getMedia = this._site ? getMedia.bind(this, this.site) : null
    public getView = this._site ? getView.bind(this, this.site) : null
    public getLatestMedia = this._site ? getLatestMedia.bind(this, this.site) : null
    public getPublicInfo = this._site ? getPublicInfo.bind(this, this.site) : null
    public getCollection = this._site ? getCollection.bind(this, this.site) : null
    public getResume = this._site ? getResume.bind(this, this.site) : null
    public getRecommendations = this._site ? getRecommendations.bind(this, this.site) : null
    public getEpisodes = this._site ? getEpisodes.bind(this, this.site) : null
    public getSeasons = this._site ? getSeasons.bind(this, this.site) : null
    public getItemWithName = this._site ? lookupItem.bind(this, this.site) : null
    public searchRecommend = this._site ? searchRecommend.bind(this, this.site) : null
    public imageUrl = this._site ? imageUrl.bind(this, this.site) : null
    public videoUrl = this._site ? playUrl.bind(this, this.site) : null
}

export const Api = {
    emby: null as Emby|null,
    login: login
};