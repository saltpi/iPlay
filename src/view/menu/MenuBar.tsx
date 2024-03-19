import {TabNavigation} from '@global';
import { useAppDispatch, useAppSelector } from '@hook/store';
import {useNavigation, useRoute} from '@react-navigation/native';
import {getActiveMenu, switchToMenu} from '@store/menuSlice';
import { Animated, StyleSheet, TouchableOpacity, View} from 'react-native';
import { Image } from '@view/Image';
import { OSType, isOS } from '@helper/device';
import { useEffect, useMemo, useRef } from 'react';
import { switchRoute } from '@store/themeSlice';
const homeIcon = require('@view/menu/Home.png');
const searchIcon = require('@view/menu/Search.png');
const starIcon = require('@view/menu/Star.png');
const settingsIcon = require('@view/menu/Setting.png');
const messageIcon = require('@view/settings/message.png');

export enum MenuType {
    Home = 'HomeTab',
    Search = 'SearchTab',
    Star = 'StarTab',
    Settings = 'SettingsTab',
    Message = 'MessageTab',
}

const style = StyleSheet.create({
    menuBar: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        borderTopColor: 'gray',
        borderTopWidth: 0.25,
        paddingTop: 8,
        paddingBottom: 2.5,
        alignItems: 'center',
        flexShrink: 0,
        flexGrow: 0,
        flexBasis: 'auto',
    },
    menuItem: {
        flex: 1,
        alignItems: 'center',
        textAlign: 'center',
    },
    icon: {
        height: 25,
        aspectRatio: 1,
        // tintColor: "rgba(128, 128, 128, 0.3)",
        opacity: 0.25,
    },
    activeIcon: {
        height: 25,
        aspectRatio: 1,
        tintColor: undefined,
    },
});

const menu = [
    {icon: searchIcon, name: 'Search', type: MenuType.Search},
    {icon: starIcon, name: 'Star', type: MenuType.Star},
    {icon: homeIcon, name: 'Home', type: MenuType.Home},
    {icon: messageIcon, name: 'Message', type: MenuType.Message},
    {icon: settingsIcon, name: 'Settings', type: MenuType.Settings},
];

export function MenuBarOld() {
    const active = useAppSelector(getActiveMenu);
    const dispatch = useAppDispatch();
    const navigation: TabNavigation = useNavigation();
    const setActive = (menu: MenuType) => {
        dispatch(switchToMenu(menu));
        navigation.navigate(menu);
    };

    const showMenuBar = useAppSelector(state => state.theme.showMenuBar)
    
    const menuBarStyle = useMemo(() => {
        if (isOS(OSType.Android)) {
            return {
                ...style.menuBar,
                paddingBottom: 15
            }
        }
        return {
            ...style.menuBar
            
        }
    }, [showMenuBar]);

    return (
        <View style={menuBarStyle}>
            {menu.map((item, i) => (
                <TouchableOpacity activeOpacity={1.0}
                    key={i}
                    style={style.menuItem}
                    onPress={() => setActive(item.type)}>
                    <View>
                        <Image
                            style={
                                active === item.type
                                    ? style.activeIcon
                                    : style.icon
                            }
                            source={item.icon}
                        />
                    </View>
                </TouchableOpacity>
            ))}
        </View>
    );
}

export function RouteMenuBar() {
    const route = useRoute()
    const dispatch = useAppDispatch()
    useEffect(() => {
        dispatch(switchRoute(route.name))
    }, [route.name])
    return null
}

export function MenuBar() {
  const active = useAppSelector(getActiveMenu);
  const dispatch = useAppDispatch();
  const navigation: TabNavigation = useNavigation();
  const showMenuBar = useAppSelector(state => state.theme.showMenuBar)

  const position = useRef(new Animated.Value(showMenuBar ? 0 : 100)).current; // Assuming the height of the component is less than 100

  useEffect(() => {
    Animated.timing(position, {
      toValue: showMenuBar ? 0 : 100,
      duration: 200,
      useNativeDriver: true,
    }).start();
  }, [showMenuBar]);

  const setActive = (menu: MenuType) => {
    dispatch(switchToMenu(menu));
    navigation.navigate(menu);
  };

  const menuBarStyle = useMemo(() => {
    if (isOS(OSType.Android)) {
      return {
        ...style.menuBar,
        paddingBottom: 15
      }
    }
    return {
      ...style.menuBar
    }
  }, [showMenuBar]);

  return (
    <Animated.View style={{...menuBarStyle, transform: [{ translateY: position }],}}>
      {menu.map((item, i) => (
        <TouchableOpacity activeOpacity={1.0}
          key={i}
          style={style.menuItem}
          onPress={() => setActive(item.type)}>
          <View>
            <Image
              style={
                active === item.type
                  ? style.activeIcon
                  : style.icon
              }
              source={item.icon}
            />
          </View>
        </TouchableOpacity>
      ))}
    </Animated.View>
  );
}
