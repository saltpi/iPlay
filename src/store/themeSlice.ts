import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '@store';

interface ThemeState {
    routeName: string;
    showMenuBar: boolean;
}

const initialState: ThemeState = {
    routeName: 'home',
    showMenuBar: true,
};

export const themeSlice = createSlice({
    name: 'theme',
    initialState,
    reducers: {
        switchRoute: (state, action: PayloadAction<string>) => {
            const routeName = action.payload;
            const whitelist = ['home', 'settings', 'search', 'star', 'message'];
            if (whitelist.includes(routeName)) {
                state.showMenuBar = true;
            } else {
                state.showMenuBar = false;
            }
        },
    },
});

export const { switchRoute } = themeSlice.actions;

export default themeSlice.reducer;