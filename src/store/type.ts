import { createAsyncThunk } from "@reduxjs/toolkit"
import { AppDispatch, RootState } from "./store"
import { Api } from "@api/emby"

export const createAppAsyncThunk = createAsyncThunk.withTypes<{
    state: RootState
    dispatch: AppDispatch
    rejectValue: string
    extra: typeof Api
}>()