import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import _ from 'lodash';

interface ConfigState {
    video: {
        useInternalMPV?: boolean;
    }
}

type ThemeUpdateFunction = (state: ConfigState) => ConfigState;

const initialState: ConfigState = {
    video: {
        useInternalMPV: false
    }
};

export const slice = createSlice({
    name: 'config',
    initialState,
    reducers: {
        updateConfig:(state, action: PayloadAction<Partial<ConfigState>|ThemeUpdateFunction>) => {
            if (typeof action.payload === 'function') {
                action.payload(state);
            } else {
                _.merge(state, action.payload)
            }
        }
    },
});

export const { 
    updateConfig
} = slice.actions;

export default slice.reducer;