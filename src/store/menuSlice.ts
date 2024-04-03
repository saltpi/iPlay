import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { RootState } from '@store';
import { MenuType } from '@view/menu/MenuBar';

interface MenuState {
    value: MenuType;
    showSwitchSiteDialog?: boolean;
}

const initialState: MenuState = {
    value: MenuType.Home,
    showSwitchSiteDialog: false
};

export const slice = createSlice({
    name: 'menu',
    initialState,
    reducers: {
        // Use the PayloadAction type to declare the contents of `action.payload`
        switchToMenu: (state, action: PayloadAction<MenuType>) => {
            state.value = action.payload;
        },
        toggleSwitchSiteDialog: (state) => {
            state.showSwitchSiteDialog = !state.showSwitchSiteDialog;
        }
    },
    extraReducers: builder => {
    },
});

export const { switchToMenu, toggleSwitchSiteDialog } = slice.actions;

// The function below is called a selector and allows us to select a value from
// the state. Selectors can also be defined inline where they're used instead of
// in the slice file. For example: `useSelector((state: RootState) => state.counter.value)`
export const getActiveMenu = (state: RootState) => state.menu.value;

export default slice.reducer;