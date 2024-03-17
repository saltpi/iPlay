import { MediaSource } from "@model/PlaybackInfo"
export interface MediaDetail {
    Name: string
    OriginalTitle: string
    Overview: string
    Genres: string[]
    BackdropImageTags: string[]
    CollectionType: "tvshows"|"movies"
    Id: string
    Etag: string
    Type: "Series"|"Movie"|string
    ImageTags: {
        Banner: string
        Logo: string
        Primary: string
        Thumb: string
    }
    People: People[]
    SeriesId: string
    SeriesName: string
    MediaSources?: MediaSource[]
}

export interface People {
    Id: string
    Name: string
    PrimaryImageTag: string
    Role: string
    Type: string
}