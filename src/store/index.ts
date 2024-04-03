import { combineReducers, configureStore } from '@reduxjs/toolkit';
import menuReducer from './menuSlice';
import embyReducer from './embySlice';
import themeReducer from './themeSlice';
import configReducer from './configSlice';
import playerReducer from './playerSlice';
import { listener } from "./middleware/Listener";
import { thunk } from 'redux-thunk';
import { Api } from '@api/emby';
import { FLUSH, PAUSE, PERSIST, PURGE, REGISTER, REHYDRATE, persistReducer, persistStore } from 'redux-persist';
import { reduxStorage } from '@helper/storage';


const Env = {
  name: "development",
  storeKey: "dev"
}

const storage = reduxStorage

const persistConfig = {
  key: [Env.storeKey, "root"].join("/"),
  storage,
  blacklist: [
      "menu",
      "theme",
      "player"
  ]
}


const reducer = combineReducers({
  theme: persistReducer({
      key: [Env.storeKey, "theme"].join("/"),
      blacklist: [
        "routeName",
        "hideMenuBar",
        "menuBarTopY"
      ],
      storage
  }, themeReducer),
  config: persistReducer({
      key: [Env.storeKey, "config"].join("/"),
      storage
  }, configReducer),
  menu: persistReducer({
      key: [Env.storeKey, "menu"].join("/"),
      blacklist: [
        "value"
      ],
      storage
  }, menuReducer),
  emby: persistReducer({
      key: [Env.storeKey, "emby"].join("/"),
      storage
  }, embyReducer),
  player: persistReducer({
      key: [Env.storeKey, "player"].join("/"),
      storage,
      blacklist: [
        "status"
      ]
  }, playerReducer),
})

const persistedReducer = persistReducer(persistConfig, reducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({
    serializableCheck: false,
    // serializableCheck: {
    //   ignoredPaths: [
    //     "emby.emby",
    //     "theme.routeName",
    //     "theme.hideMenuBar",
    //   ],
    //   ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
    // },
    thunk: {
      extraArgument: Api
    },
  }).concat(thunk, listener)
});

export const persistor = persistStore(store, null, () => {
  store.dispatch({ type: REHYDRATE });
})

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>
// Inferred type: {posts: PostsState, comments: CommentsState, users: UsersState}
export type AppDispatch = typeof store.dispatch
