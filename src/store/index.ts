import { configureStore } from '@reduxjs/toolkit';
import counterReducer from './counterSlice';
import menuReducer from './menuSlice';
import embyReducer from './embySlice';
import themeReducer from './themeSlice';
import { listener } from "./middleware/Listener";
import { thunk } from 'redux-thunk';
import { Api } from '@api/emby';

export const store = configureStore({
  reducer: {
    counter: counterReducer,
    menu: menuReducer,
    emby: embyReducer,
    theme: themeReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({
    serializableCheck: false,
    thunk: {
      extraArgument: Api
    },
  }).concat(thunk, listener)
});

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch
