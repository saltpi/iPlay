import { UserData } from "./UserData";

export interface Episode {
    Id: string;
    Name: string;
    Overview: string;
    SeriesId: string;
    SeriesName: string;
    SeasonId: string;
    SeasonName: string;
    PrimaryImageAspectRatio: number;
    IndexNumber: number;
    ImageTags: {
        Primary: string;
    };
    UserData: UserData;
}
