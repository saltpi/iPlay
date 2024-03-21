import { Page as AlbumPage } from "@page/album/index.tsx";
import { Page as HomePage } from "@page/home/index.tsx";
import { Page as LoginPage } from "@page/login/index.tsx";
import { Page as MessagePage } from "@page/message/index.tsx";
import { Page as MoviePage } from "@page/movie/index.tsx";
import { Page as SearchPage } from "@page/search/index.tsx";
import { Page as SeasonPage } from "@page/season/index.tsx";
import { Page as SettingsPage } from "@page/settings/index.tsx";
import { Page as StarPage } from "@page/star/index.tsx";
import { Page as PlayerPage } from "@page/player/index.tsx";
import { Page as ThemePage } from "@page/theme/index.tsx";
import { Page as TestPage } from "@page/test/index.tsx";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { NavigationContainer, NavigationState } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { MenuBar, MenuType } from "@view/menu/MenuBar";
import React from "react";
import { useAppDispatch } from "@hook/store";
import { switchRoute } from "@store/themeSlice";


const HomeStack = createNativeStackNavigator();
const SettingsStack = createNativeStackNavigator();
const SearchStack = createNativeStackNavigator();
const StarStack = createNativeStackNavigator();
const MessageStack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();
const defaultOptions = (options: any) => {
    return {
        title: (options.route.params as any)?.title ?? '',
    };
};
const fullscreenOptions = (options: any) => {
    return {
        title: (options.route.params as any)?.title ?? '',
    };
};
const HomeRouter = () => (
    <HomeStack.Navigator initialRouteName="home">
        <HomeStack.Screen
            name="login"
            component={LoginPage}
            options={{ title: '登录' }} />
        <HomeStack.Screen
            name="home"
            component={HomePage as any}
            options={{ title: '主页' }} />
        <HomeStack.Screen
            name="album"
            component={AlbumPage as any}
            options={defaultOptions} />
        <HomeStack.Screen
            name="movie"
            component={MoviePage as any}
            options={defaultOptions} />
        <HomeStack.Screen
            name="season"
            component={SeasonPage as any}
            options={defaultOptions} />
        <HomeStack.Screen
            name="player"
            component={PlayerPage as any}
            options={fullscreenOptions} />
    </HomeStack.Navigator>
);
const SettingsRouter = () => (
    <SettingsStack.Navigator initialRouteName="settings">
        <SettingsStack.Screen
            name="settings"
            component={SettingsPage as any}
            options={{ title: '设置' }} />
        <SettingsStack.Screen
            name="login"
            component={LoginPage}
            options={{ title: '登录' }} />
        <SettingsStack.Screen
            name="theme"
            component={ThemePage}
            options={{ title: '主题' }} />
    </SettingsStack.Navigator>
);
const SearchRouter = () => (
    <SearchStack.Navigator initialRouteName="search">
        <SearchStack.Screen
            name="search"
            component={SearchPage}
            options={{ title: '搜索' }} />
                <HomeStack.Screen
            name="movie"
            component={MoviePage as any}
            options={defaultOptions} />
        <SearchStack.Screen
            name="season"
            component={SeasonPage as any}
            options={defaultOptions} />
        <SearchStack.Screen
            name="player"
            component={PlayerPage as any}
            options={fullscreenOptions} />
    </SearchStack.Navigator>
);
const MessageRouter = () => (
    <MessageStack.Navigator initialRouteName="search">
        <MessageStack.Screen
            name="search"
            component={MessagePage}
            options={{ title: '消息' }} />
    </MessageStack.Navigator>
);
const StarRouter = () => (
    <StarStack.Navigator initialRouteName="star">
        <StarStack.Screen
            name="star"
            component={StarPage}
            options={{ title: '收藏' }} />
        <StarStack.Screen
            name="test"
            component={TestPage}
            options={{ title: '测试' }} />
    </StarStack.Navigator>
);

function getActiveRouteName(state: NavigationState) {
    const route = state.routes[state.index];
    if (route.state) {
      return getActiveRouteName(route.state as any);
    }
    return route.name;
}

export function Router() {
    const dispatch = useAppDispatch();
    return (
        <NavigationContainer onStateChange={s => dispatch(switchRoute(getActiveRouteName(s)))}>
            <Tab.Navigator
                initialRouteName="home"
                tabBar={() => null}
                screenOptions={{ headerShown: false }}>
                <Tab.Screen name={MenuType.Home} component={HomeRouter} />
                <Tab.Screen
                    name={MenuType.Settings}
                    component={SettingsRouter} />
                <Tab.Screen name={MenuType.Search} component={SearchRouter} />
                <Tab.Screen name={MenuType.Star} component={StarRouter} />
                <Tab.Screen name={MenuType.Message} component={MessageRouter} />
            </Tab.Navigator>
            <MenuBar />
        </NavigationContainer>
    );
}
