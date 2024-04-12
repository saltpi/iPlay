import { ThemeBasicStyle } from '@global';
import { Dev } from '@helper/dev';
import { Device, isOS, OSType } from '@helper/device';
import { createSelector, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '@store';
import _ from 'lodash';
import { EdgeInsets } from 'react-native-safe-area-context';

export enum ColorScheme {
    Auto,
    Light,
    Dark
}

export enum LayoutType {
    Card,
    Line,
}

interface ThemeState {
    routeName: string;
    hideMenuBar: boolean;
    colorScheme: ColorScheme;
    // menu bar padding bottom offset
    menuBarPaddingOffset: number;
    menuBarHeight?: number;
    statusBarHeight: number,
    pagePaddingTop: number,
    safeInsets: EdgeInsets;
    showVideoLink?: boolean;
    isDarkMode: boolean;
    fontColor?: string;
    fontSize?: number;
    fontFamily?: string;
    backgroundColor?: string;
    barStyle?: 'default' | 'light-content' | 'dark-content';
    headerTitleAlign?: 'left' | 'center';
    albumLayoutType?: LayoutType;
}

type ThemeUpdateFunction = (state: ThemeState) => ThemeState;

const initialState: ThemeState = {
    routeName: 'home',
    colorScheme: ColorScheme.Auto,
    safeInsets: {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    },
    hideMenuBar: false,
    menuBarPaddingOffset: 0,
    statusBarHeight: 0,
    showVideoLink: false,
    isDarkMode: false,
    headerTitleAlign: 'center',
    pagePaddingTop: Device.isDesktop ? 0 : 56,
};

export const slice = createSlice({
    name: 'theme',
    initialState,
    reducers: {
        switchRoute: (state, action: PayloadAction<string>) => {
            const routeName = action.payload;
            const whitelist = ['home', 'settings', 'search', 'star', 'message', 'test'];
            if (whitelist.includes(routeName)) {
                state.hideMenuBar = false;
            } else {
                state.hideMenuBar = true;
            }
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
        },
        updateToNextAlbumLayoutType: (state) => {
            state.albumLayoutType = state.albumLayoutType === LayoutType.Card ? LayoutType.Line : LayoutType.Card;
        }
    },
});

const getHeaderTintColor = (state: RootState) => state.theme.fontColor;
const getFontFamily = (state: RootState) => state.theme.fontFamily;
const getBackgroundColor = (state: RootState) => state.theme.backgroundColor;
const getHeaderTitleAlign = (state: RootState) => state.theme.headerTitleAlign;
const getPagePaddingTop = (state: RootState) => state.theme.pagePaddingTop;
const getMenuBarHeight = (state: RootState) => state.theme.menuBarHeight;

export const selectScreenOptions = createSelector([
    getHeaderTintColor,
    getBackgroundColor,
    getHeaderTitleAlign
], (headerTintColor, backgroundColor, headerTitleAlign) => {
    const options = {
        headerTitleAlign,
        headerStyle: {
            backgroundColor
        }, 
        headerTransparent: true,
        headerTintColor,
        contentStyle: {
            backgroundColor,
        },
    }
    if (Device.isDesktop) return {
        // headerTitleAlign,
        headerShown: false,
        headerStyle: {
            backgroundColor: 'transparent',
        }, 
        headerTintColor,
        contentStyle: {
            backgroundColor,
        },
    }
    return options as {};
})

export const selectThemeBasicStyle = createSelector([
    getHeaderTintColor,
    getBackgroundColor,
    getFontFamily
], (color, backgroundColor, fontFamily) => {
    return {color, backgroundColor, fontFamily} as ThemeBasicStyle;
})

export const selectThemedPageStyle = createSelector([
    getHeaderTintColor,
    getBackgroundColor,
    getPagePaddingTop,
    getMenuBarHeight,
    getFontFamily
], (color, backgroundColor, paddingTop, paddingBottom, fontFamily) => {
    return {
        color, 
        backgroundColor,
        paddingTop,
        paddingBottom,
        fontFamily
    };
})

export const { 
    switchRoute, 
    updateMenuBarPaddingOffset, 
    updateMenuBarHeight,
    updateShowVideoLink,
    updateTheme,
    updateToNextAlbumLayoutType
} = slice.actions;

export default slice.reducer;