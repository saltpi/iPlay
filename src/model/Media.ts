import { UserData } from "./UserData"

export interface Media {
    SortName: String
    DateCreated: String
    AirDays: string[]
    BackdropImageTags: string[]
    CanDelete: boolean
    Id: string
    ParentBackdropItemId: string
    ParentBackdropImageTags: string[]
    ParentThumbItemId: string
    ParentThumbImageTag: string
    SeasonId: string
    SeasonName: string
    SeriesId: string
    SeriesName: string
    ImageTags: {
        Primary: string,
        Thumb: string
    }
    Primary: string
    Thumb: string
    IsFolder: boolean
    Name: string
    Overview: string
    PrimaryImageAspectRatio: number
    ProductionYear: number
    RunTimeTicks: number
    ServerId: string
    Status: string
    SupportsSync: boolean
    Type: "Series"|"Movie"|"Episode"
    UserData: UserData
}