import { Episode } from "@model/Episode";
import { Media } from '@model/Media';
import { People } from "@model/MediaDetail";
import { PlaybackInfo } from '@model/PlaybackInfo';
import { NavigationProp, ParamListBase, RouteProp, Router } from '@react-navigation/native';
import { MenuType } from '@view/menu/MenuBar';
import React from 'react';

type RootStackParamList = {
  home: undefined;
  album: {
    title: string,
    albumId: string;
    albumName: string
  },
  movie: {
    title: string,
    type: string,
    movie: Media
  },
  login: undefined,
  season: {
    title: string,
    season: Season
  },
  player: {
    title: string,
    episode: Episode,
    episodes: Episode[],
  },
  theme: undefined,
  test: undefined,
  config_video: undefined,
  about: undefined,
  default: undefined,
  actor: {
    title?: string
    id?: string
    actor?: People
  }
};

type TabStackParamList = {
  [MenuType.Home]: undefined;
  [MenuType.Search]: undefined;
  [MenuType.Star]: undefined;
  [MenuType.Settings]: undefined;
  [MenuType.Message]: undefined;
};

type ThemeBasicStyle = {
  backgroundColor: string,
  color?: string,
}

type Navigation = NavigationProp<RootStackParamList>;
type TabNavigation = NavigationProp<TabStackParamList>;

type PropsWithNavigation<K extends keyof RootStackParamList> = {
  navigation: Navigation;
  route: RouteProp<RootStackParamList, K>;
}