import { createAsyncThunk } from "@reduxjs/toolkit"
import { RootState } from "@store"
import { Api } from "@api/emby"

export const createAppAsyncThunk = createAsyncThunk.withTypes<{
    state: RootState
    rejectValue: string
    extra: typeof Api
}>()