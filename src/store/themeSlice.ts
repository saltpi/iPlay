import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ThemeState {
    routeName: string;
    showMenuBar: boolean;
    // menu bar padding bottom offset
    menuBarPaddingOffset: number;
}

const initialState: ThemeState = {
    routeName: 'home',
    showMenuBar: true,
    menuBarPaddingOffset: 0,
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
        updateMenuBarPaddingOffset: (state, action: PayloadAction<number>) => {
            state.menuBarPaddingOffset = action.payload;
        }
    },
});

export const { switchRoute, updateMenuBarPaddingOffset } = themeSlice.actions;

export default themeSlice.reducer;