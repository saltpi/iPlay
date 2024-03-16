export interface ViewDetail {
    BackdropImageTags: string[]
    CanDelete: boolean
    CanDownload: boolean
    ChildCount: number
    CollectionType: string
    DateCreated: string
    DisplayPreferencesId: string
    Etag: string
    ExternalUrls: string[]
    FileName: string
    ForcedSortName: string
    Guid: string
    Id: string
    ImageTags: {
        Primary: string
    }
    Primary: string
    IsFolder: boolean
    LockData: boolean
    LockedFields: string[]
    Name: string
    ParentId: string
    Path: string
    PlayAccess: string
    PresentationUniqueKey: string
    PrimaryImageAspectRatio: number
    ProviderIds: {}
    RemoteTrailers: string[]
    ServerId: string
    SortName: string
    Taglines: string[]
    Type: string
}

export interface View {
    TotalRecordCount: number
    Items: ViewDetail[]
}