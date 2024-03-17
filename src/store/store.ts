import { configureStore } from '@reduxjs/toolkit';
import counterReducer from './counterSlice';
import menuReducer from './menuSlice';
import embyReducer from './embySlice';
import { listener } from "./middleware/Listener";

export const store = configureStore({
  reducer: {
    counter: counterReducer,
    menu: menuReducer,
    emby: embyReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(listener)
});

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch