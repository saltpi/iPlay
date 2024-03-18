import { Media } from '@model/Media';
import { PlaybackInfo } from '@model/PlaybackInfo';
import { NavigationProp, ParamListBase, RouteProp, Router } from '@react-navigation/native';
import { MenuType } from '@view/menu/MenuBar';

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
    poster?: string
    media: PlaybackInfo,
  },
  default: undefined,
};

type TabStackParamList = {
  [MenuType.Home]: undefined;
  [MenuType.Search]: undefined;
  [MenuType.Star]: undefined;
  [MenuType.Settings]: undefined;
};

type Navigation = NavigationProp<RootStackParamList>;
type TabNavigation = NavigationProp<TabStackParamList>;

type PropsWithNavigation<K extends keyof RootStackParamList> = {
  navigation: Navigation;
  route: RouteProp<RootStackParamList, K>;
}