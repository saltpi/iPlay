import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface ThemeState {
    routeName: string;
    hideMenuBar: boolean;
    // menu bar padding bottom offset
    menuBarPaddingOffset: number;
    menuBarHeight?: number;
}

const initialState: ThemeState = {
    routeName: 'home',
    hideMenuBar: false,
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
                state.hideMenuBar = false;
            } else {
                state.hideMenuBar = true;
            }
            console.log(`switchRoute: ${routeName} ${state.hideMenuBar}`)
        },
        updateMenuBarPaddingOffset: (state, action: PayloadAction<number>) => {
            state.menuBarPaddingOffset = action.payload;
        },
        updateMenuBarHeight: (state, action: PayloadAction<number>) => {
            state.menuBarHeight = action.payload;
        }
    },
});

export const { switchRoute, updateMenuBarPaddingOffset, updateMenuBarHeight } = themeSlice.actions;

export default themeSlice.reducer;