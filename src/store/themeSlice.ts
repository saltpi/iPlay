import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import _ from 'lodash';

interface ThemeState {
    routeName: string;
    hideMenuBar: boolean;
    // menu bar padding bottom offset
    menuBarPaddingOffset: number;
    menuBarHeight?: number;
    showVideoLink?: boolean;
}

type ThemeUpdateFunction = (state: ThemeState) => ThemeState;

const initialState: ThemeState = {
    routeName: 'home',
    hideMenuBar: false,
    menuBarPaddingOffset: 0,
    showVideoLink: false,
};

export const slice = createSlice({
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
        },
        updateShowVideoLink: (state, action: PayloadAction<boolean>) => {
            state.showVideoLink = action.payload;
        },
        updateTheme:(state, action: PayloadAction<Partial<ThemeState>|ThemeUpdateFunction>) => {
            if (typeof action.payload === 'function') {
                action.payload(state);
            } else {
                _.merge(state, action.payload)
            }
        }
    },
});

export const { 
    switchRoute, 
    updateMenuBarPaddingOffset, 
    updateMenuBarHeight,
    updateShowVideoLink
} = slice.actions;

export default slice.reducer;