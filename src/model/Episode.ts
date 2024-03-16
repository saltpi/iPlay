export interface Episode {
    Id: string
    Name: string
    Overview: string
    SeriesId: string
    SeriesName: string
    SeasonId: string
    SeasonName: string
    PrimaryImageAspectRatio: number
    ImageTags: {
        Primary: string
    }
}