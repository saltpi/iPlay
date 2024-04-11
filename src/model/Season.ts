import { UserData } from "./UserData"

export interface Season {
    BackdropImageTags: string[]
    CanDelete: boolean
    Id: string
    ImageTags: { 
        Banner: string
        Logo: string
        Primary: string
        Thumb: string
    }
    IndexNumber: number
    IsFolder: boolean
    Name: string
    Overview?: string
    PrimaryImageAspectRatio: number
    SeriesId: string
    SeriesName: string
    SeriesPrimaryImageTag: string
    ServerId: string
    SupportsSync: boolean
    Type: string
    UserData: UserData
}