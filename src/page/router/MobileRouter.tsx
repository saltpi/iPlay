import {Page as AlbumPage} from '@page/album/index.tsx';
import {Page as HomePage} from '@page/home/index.tsx';
import {Page as LoginPage} from '@page/login/index.tsx';
import {Page as MessagePage} from '@page/message/index.tsx';
import {Page as MoviePage} from '@page/movie/index.tsx';
import {Page as SearchPage} from '@page/search/index.tsx';
import {Page as SeasonPage} from '@page/season/index.tsx';
import {Page as SettingsPage} from '@page/settings/index.tsx';
import {Page as StarPage} from '@page/star/index.tsx';
import {Page as PlayerPage} from '@page/player/index.tsx';
import {Page as ThemePage} from '@page/settings/theme';
import {Page as VideoConfigPage} from '@page/settings/video/index.tsx';
import {Page as TestPage} from '@page/test/index.tsx';
import {Page as ActorPage} from '@page/actor/index.tsx';
import {Page as AboutPage} from '@page/settings/about/index.tsx';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {
    DefaultTheme,
    NavigationContainer,
    NavigationState,
    Theme,
} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {MenuBar, MenuType} from '@view/menu/MenuBar';
import React, { useEffect } from 'react';
import {useAppDispatch, useAppSelector} from '@hook/store';
import {
    ColorScheme,
    selectScreenOptions,
    selectThemeBasicStyle,
    switchRoute,
    updateTheme,
} from '@store/themeSlice';
import { Colors } from 'react-native/Libraries/NewAppScreen';
import { Text, useColorScheme } from 'react-native';
import { Dev } from '@helper/dev';
import { HeaderRightAction } from '../album/HeaerRightAction';
import { Device, OSType, isOS } from '@helper/device';
import { NavBar } from '@view/menu/NavBar';
import NativeTitleBar from '@api/native/windows/NativeTitleBar';

const HomeStack = createNativeStackNavigator();
const SettingsStack = createNativeStackNavigator();
const SearchStack = createNativeStackNavigator();
const StarStack = createNativeStackNavigator();
const MessageStack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

const OptionWithTitle = (kv: any) => {
    return (options: any) => ({
        title: (options.route.params as any)?.title ?? '',
        ...kv
    })
}

const immersiveOptions = (options: any) => (Device.isDesktop ? {
    headerMode: 'float',
} : {
    title: (options.route.params as any)?.title ?? '',
    headerTransparent: true,
    headerStyle: {backgroundColor: 'transparent'},
    headerRight: options.route.name === "album" ? HeaderRightAction : null,
});

const HomeRouter = () => {
    const options = useAppSelector(selectScreenOptions);
    return (
        <HomeStack.Navigator initialRouteName="home" screenOptions={options}>
            <HomeStack.Screen
                name="login"
                component={LoginPage}
                options={{title: '登录'}}
            />
            <HomeStack.Screen
                name="home"
                component={HomePage as any}
                options={{title: '主页'}}
            />
            <HomeStack.Screen
                name="album"
                component={AlbumPage as any}
                options={immersiveOptions}
            />
            <HomeStack.Screen
                name="movie"
                component={MoviePage as any}
                options={immersiveOptions}
            />
            <HomeStack.Screen
                name="season"
                component={SeasonPage as any}
                options={immersiveOptions}
            />
            <HomeStack.Screen
                name="player"
                component={PlayerPage as any}
                options={options}
            />
            <HomeStack.Screen
                name="actor"
                component={ActorPage as any}
                options={OptionWithTitle(options)}
            />
        </HomeStack.Navigator>
    );
};
const SettingsRouter = () => {
    const options = useAppSelector(selectScreenOptions);
    return (
        <SettingsStack.Navigator
            initialRouteName="settings"
            screenOptions={options}>
            <SettingsStack.Screen
                name="settings"
                component={SettingsPage as any}
                options={{title: '设置'}}
            />
            <SettingsStack.Screen
                name="login"
                component={LoginPage}
                options={{title: '站点配置'}}
            />
            <SettingsStack.Screen
                name="theme"
                component={ThemePage}
                options={{title: '主题配置'}}
            />
            <SettingsStack.Screen
                name="config_video"
                component={VideoConfigPage}
                options={{title: '视频配置'}}
            />
            <SettingsStack.Screen
                name="about"
                component={AboutPage as any}
                options={{title: '关于我们'}}
            />
        </SettingsStack.Navigator>
    );
};
const SearchRouter = () => {
    const options = useAppSelector(selectScreenOptions);
    return (
        <SearchStack.Navigator
            initialRouteName="search"
            screenOptions={options}>
            <SearchStack.Screen
                name="search"
                component={SearchPage}
                options={{title: '搜索'}}
            />
            <SearchStack.Screen
                name="movie"
                component={MoviePage as any}
                options={immersiveOptions}
            />
            <SearchStack.Screen
                name="season"
                component={SeasonPage as any}
                options={immersiveOptions}
            />
            <SearchStack.Screen
                name="player"
                component={PlayerPage as any}
                options={immersiveOptions}
            />
            <SearchStack.Screen
                name="actor"
                component={ActorPage as any}
                options={OptionWithTitle(options)}
            />
        </SearchStack.Navigator>
    );
};
const MessageRouter = () => {
    const options = useAppSelector(selectScreenOptions);
    return (
        <MessageStack.Navigator
            initialRouteName={Dev.mode == "development" ? "test" : "message"}
            screenOptions={options}>
            <MessageStack.Screen
                name="message"
                component={MessagePage as any}
                options={{title: '消息'}}
            />
            <MessageStack.Screen
                name="movie"
                component={MoviePage as any}
                options={immersiveOptions}
            />
            <MessageStack.Screen
                name="actor"
                component={ActorPage as any}
                options={OptionWithTitle(options)}
            />
            <MessageStack.Screen
                name="test"
                component={TestPage as any}
                options={{title: '测试'}}
            />
        </MessageStack.Navigator>
    );
};
const StarRouter = () => {
    const options = useAppSelector(selectScreenOptions);
    return (
        <StarStack.Navigator
            initialRouteName="star"
            screenOptions={options}>
            <StarStack.Screen
                name="star"
                component={StarPage as any}
                options={{
                    title: '收藏',
                }}
            />
            <StarStack.Screen
                name="movie"
                component={MoviePage as any}
                options={immersiveOptions}
            />
            <StarStack.Screen
                name="season"
                component={SeasonPage as any}
                options={immersiveOptions}
            />
            <StarStack.Screen
                name="player"
                component={PlayerPage as any}
                options={immersiveOptions}
            />
            <StarStack.Screen
                name="test"
                component={TestPage}
                options={{title: '测试'}}
            />
            <StarStack.Screen
                name="actor"
                component={ActorPage as any}
                options={OptionWithTitle(options)}
            />
        </StarStack.Navigator>
    );
};

function getActiveRouteName(state: NavigationState) {
    const route = state.routes[state.index];
    if (Device.isDesktop) {
        const titile = (route.params as any)?.title;
        NativeTitleBar?.setTitle(titile ?? "No title")
    }
    if (route.state) {
        return getActiveRouteName(route.state as any);
    }
    return route.name;
}

export function Router() {
    const dispatch = useAppDispatch();
    const options = useAppSelector(selectScreenOptions);
    const theme = useAppSelector(selectThemeBasicStyle);
    const colorScheme = useAppSelector(state => state.theme.colorScheme);
    const isDarkMode = useAppSelector(state => state.theme.isDarkMode);
    const isSysDarkMode = useColorScheme() === 'dark';
    // @ref https://reactnavigation.org/docs/themes/
    const pageTheme: Theme = {
        ...DefaultTheme,
        dark: isDarkMode,
        colors: {
            ...DefaultTheme.colors,
            text: theme.color ?? DefaultTheme.colors.text,
            background: theme.backgroundColor,
        },
    };

    useEffect(() => {
        dispatch(updateTheme(theme => {
            const isDarkMode = (
                (theme.colorScheme === ColorScheme.Auto && isSysDarkMode) ||
                theme.colorScheme === ColorScheme.Dark
            );
            theme.fontColor = isDarkMode ? Colors.light : Colors.dark;
            theme.backgroundColor = isDarkMode ? Colors.darker : Colors.lighter;
            theme.barStyle = isDarkMode ? 'light-content' : 'dark-content';
            return theme
        }))
    }, [colorScheme])

    return (
        <NavigationContainer
            theme={pageTheme}
            onStateChange={s => dispatch(switchRoute(getActiveRouteName(s)))}>
            <Tab.Navigator
                initialRouteName={MenuType.Home}
                tabBar={() => null}
                screenOptions={{
                    headerShown: false,
                    ...options,
                }}>
                <Tab.Screen name={MenuType.Home} component={HomeRouter} />
                <Tab.Screen name={MenuType.Settings} component={SettingsRouter} />
                <Tab.Screen name={MenuType.Search} component={SearchRouter} />
                <Tab.Screen name={MenuType.Star} component={StarRouter} />
                <Tab.Screen name={MenuType.Message} component={MessageRouter} />
            </Tab.Navigator>
            <MenuBar />
        </NavigationContainer>
    );
}
