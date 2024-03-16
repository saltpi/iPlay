/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useEffect } from 'react';
import {NavigationContainer} from '@react-navigation/native';
import { Api, Emby } from '@api/emby';
import { config } from '@api/config';
import { Page as HomePage } from '@page/home/index.tsx';
import { Page as AlbumPage } from '@page/album/index.tsx';
import { Page as MoviePage } from '@page/movie/index.tsx';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

function App(): React.JSX.Element {
  const defaultOptions = (options: any) => {
    return {
      title: (options.route.params as any)?.title ?? ""
    }
  }
  useEffect(() => {
    const emby = new Emby({
      AccessToken: "FFF",
      ServerId: "FFF",
      User: {
          Name: "guest",
          Id: "FFF",
          ServerId: "FFF"
      }
    })
    Api.emby = emby
    config.emby = {
      host: "emby.endemy.me",
      port: 443,
      protocol: "https",
      path: "/"
    }
  }, [])
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="home">
        <Stack.Screen name="home" component={HomePage} options={{title: '主页'}} />
        <Stack.Screen name="album" component={AlbumPage as any} options={defaultOptions} />
        <Stack.Screen name="movie" component={MoviePage as any} options={defaultOptions} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default App;
