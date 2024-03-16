export interface MediaSource {
    Id: string
    Container: string
    DirectStreamUrl: string
    Path: string
    TranscodingUrl: string
    Name: string
}

export interface PlaybackInfo {
    MediaSources: MediaSource[]
    PlaySessionId: string
}