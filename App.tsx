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

const Stack = createNativeStackNavigator();

function App() {
  const [inited, setInited] = useState(false)
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
        <Stack.Navigator initialRouteName="home">
          <Stack.Screen name="login" component={LoginPage} options={{title: '登录'}} />
          <Stack.Screen name="home" component={HomePage as any} options={{title: '主页'}} />
          <Stack.Screen name="album" component={AlbumPage as any} options={defaultOptions} />
          <Stack.Screen name="movie" component={MoviePage as any} options={defaultOptions} />
        </Stack.Navigator>
      </NavigationContainer>
    );
  } else {
    return null
  }
}

export default App;
