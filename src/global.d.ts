import { Media } from '@model/Media';
import { NavigationProp, ParamListBase, RouteProp, Router } from '@react-navigation/native';

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
  }
};

type Navigation = NavigationProp<RootStackParamList>;

type PropsWithNavigation<K extends keyof RootStackParamList> = {
    navigation: Navigation;
    route: RouteProp<RootStackParamList, K>;
}