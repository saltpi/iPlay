import React, {useEffect, useState} from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {Api, Emby} from '@api/emby';
import {config} from '@api/config';
import {Page as HomePage} from '@page/home/index.tsx';
import {Page as AlbumPage} from '@page/album/index.tsx';
import {Page as MoviePage} from '@page/movie/index.tsx';
import {Page as LoginPage} from '@page/login/index.tsx';
import {Page as SettingsPage} from '@page/settings/index.tsx';
import {Page as SearchPage} from '@page/search/index.tsx';
import {Page as StarPage} from '@page/star/index.tsx';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {Store} from '@helper/store';
import {MenuType} from '@view/menu/MenuBar';
import {Provider} from 'react-redux';
import {store} from '@store';
import {getActiveMenu} from '@store/menuSlice';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import { Toast, toastConfig } from '@helper/toast';
import { useAppSelector } from '@hook/store';

const HomeStack = createNativeStackNavigator();
const SettingsStack = createNativeStackNavigator();
const SearchStack = createNativeStackNavigator();
const StarStack = createNativeStackNavigator();

const Tab = createBottomTabNavigator();

const defaultOptions = (options: any) => {
    return {
        title: (options.route.params as any)?.title ?? '',
    };
};

const HomeRouter = () => (
    <HomeStack.Navigator initialRouteName="home">
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
            options={defaultOptions}
        />
        <HomeStack.Screen
            name="movie"
            component={MoviePage as any}
            options={defaultOptions}
        />
    </HomeStack.Navigator>
);

const SettingsRouter = () => (
    <SettingsStack.Navigator initialRouteName="settings">
        <SettingsStack.Screen
            name="settings"
            component={SettingsPage as any}
            options={{title: '设置'}}
        />
        <SettingsStack.Screen
            name="login"
            component={LoginPage}
            options={{title: '登录'}}
        />
    </SettingsStack.Navigator>
);

const SearchRouter = () => (
    <SearchStack.Navigator initialRouteName="search">
        <SearchStack.Screen
            name="search"
            component={SearchPage}
            options={{title: '搜索'}}
        />
    </SearchStack.Navigator>
);

const StarRouter = () => (
    <StarStack.Navigator initialRouteName="star">
        <StarStack.Screen
            name="star"
            component={StarPage}
            options={{title: '收藏'}}
        />
    </StarStack.Navigator>
);

function Router() {
    const menu = useAppSelector(getActiveMenu);
    return (
        <NavigationContainer>
            <Tab.Navigator
                initialRouteName="home"
                tabBar={() => null}
                screenOptions={{headerShown: false}}>
                <Tab.Screen name={MenuType.Home} component={HomeRouter} />
                <Tab.Screen
                    name={MenuType.Settings}
                    component={SettingsRouter}
                />
                <Tab.Screen name={MenuType.Search} component={SearchRouter} />
                <Tab.Screen name={MenuType.Star} component={StarRouter} />
            </Tab.Navigator>
        </NavigationContainer>
    );
}

function App() {
    const [inited, setInited] = useState(false);
    const init = async () => {
        const user = await Store.get('@user');
        const server = await Store.get('@server');
        if (!user || !server) {
            Api.emby = null;
            return;
        }
        const emby = new Emby(JSON.parse(user));
        config.emby = JSON.parse(server);
        Api.emby = emby;
    };
    useEffect(() => {
        init().then(() => setInited(true));
    }, []);

    if (inited) {
        return (
            <Provider store={store}>
                <Router />
                <Toast config={toastConfig} />
            </Provider>
        );
    } else {
        return null;
    }
}

export default App;
