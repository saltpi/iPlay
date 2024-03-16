/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect, useState } from 'react';
import {NavigationContainer} from '@react-navigation/native';
import { Api, Emby } from '@api/emby';
import { config } from '@api/config';
import { Page as HomePage } from '@page/home/index.tsx';
import { Page as AlbumPage } from '@page/album/index.tsx';
import { Page as MoviePage } from '@page/movie/index.tsx';
import { Page as LoginPage } from '@page/login/index.tsx';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Store } from '@helper/store';
import { MenuType } from '@view/menu/MenuBar';
import { Provider } from 'react-redux';
import { store } from '@store/store';

const HomeStack = createNativeStackNavigator();
const SettingsStack = createNativeStackNavigator();
const SearchStack = createNativeStackNavigator();
const StarStack = createNativeStackNavigator();

function App() {
  const [inited, setInited] = useState(false)
  const [menu, setMenu] = useState(MenuType.Home)
  const defaultOptions = (options: any) => {
    return {
      title: (options.route.params as any)?.title ?? ""
    }
  }
  const init = async () => {
    const user = await Store.get("@user")
    const server = await Store.get("@server")
    if (!user || !server) {
      Api.emby = null
      return
    }
    const emby = new Emby(JSON.parse(user))
    config.emby = JSON.parse(server)
    Api.emby = emby
  }
  useEffect(() => {
    init().then(() => setInited(true))
  }, [])

  if (inited) {
    return (
      <NavigationContainer>
        <Provider store={store}>
        {menu === MenuType.Home ?
        <HomeStack.Navigator initialRouteName="home">
          <HomeStack.Screen name="login" component={LoginPage} options={{title: '登录'}} />
          <HomeStack.Screen name="home" component={HomePage as any} options={{title: '主页'}} />
          <HomeStack.Screen name="album" component={AlbumPage as any} options={defaultOptions} />
          <HomeStack.Screen name="movie" component={MoviePage as any} options={defaultOptions} />
        </HomeStack.Navigator>
        : null}
        {menu === MenuType.Settings ?
        <SettingsStack.Navigator initialRouteName="settings">
          <SettingsStack.Screen name="settings" component={LoginPage} options={{title: '设置'}} />
        </SettingsStack.Navigator>
        : null}
        {menu === MenuType.Search ?
        <SearchStack.Navigator initialRouteName="search">
          <SearchStack.Screen name="search" component={LoginPage} options={{title: '搜索'}} />
        </SearchStack.Navigator>
        : null}
        {menu === MenuType.Star ?
        <StarStack.Navigator initialRouteName="star">
          <StarStack.Screen name="star" component={LoginPage} options={{title: '收藏'}} />
        </StarStack.Navigator>
        : null}
        </Provider>
      </NavigationContainer>
    );
  } else {
    return null
  }
}

export default App;
