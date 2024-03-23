import {TabNavigation} from '@global';
import {useAppDispatch, useAppSelector} from '@hook/store';
import {useNavigation, useRoute} from '@react-navigation/native';
import {getActiveMenu, switchToMenu} from '@store/menuSlice';
import {Animated, StyleSheet, TouchableOpacity, View} from 'react-native';
import {OSType, isOS} from '@helper/device';
import {useEffect, useMemo, useRef} from 'react';
import {switchRoute, updateMenuBarHeight} from '@store/themeSlice';
import {useSafeAreaInsets} from 'react-native-safe-area-context';
import HomeIcon from '@asset/home.svg';
import SearchIcon from '@asset/search.svg';
import StarIcon from '@asset/star.svg';
import SettingsIcon from '@asset/setting.svg';
import MessageIcon from '@asset/message.svg';

export enum MenuType {
    Home = 'HomeTab',
    Search = 'SearchTab',
    Star = 'StarTab',
    Settings = 'SettingsTab',
    Message = 'MessageTab',
}

const style = StyleSheet.create({
    menuBar: {
        position: 'absolute',
        left: 0,
        bottom: 0,
        flexDirection: 'row',
        justifyContent: 'space-around',
        backgroundColor: 'white',
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
    {icon: SearchIcon, name: 'Search', type: MenuType.Search},
    {icon: StarIcon, name: 'Star', type: MenuType.Star},
    {icon: HomeIcon, name: 'Home', type: MenuType.Home},
    {icon: MessageIcon, name: 'Message', type: MenuType.Message},
    {icon: SettingsIcon, name: 'Settings', type: MenuType.Settings},
];

export function RouteMenuBar() {
    const route = useRoute();
    const dispatch = useAppDispatch();
    useEffect(() => {
        dispatch(switchRoute(route.name));
    }, [route.name]);
    return null;
}

const kIconSize = {
    width: 25,
    height: 25,
}

const kInactiveOpacity = 0.25;

export function MenuBar() {
    const active = useAppSelector(getActiveMenu);
    const dispatch = useAppDispatch();
    const navigation: TabNavigation = useNavigation();
    const hideMenuBar = useAppSelector(state => state.theme.hideMenuBar);
    const menuBarPaddingOffset = useAppSelector(state => state.theme.menuBarPaddingOffset);
    const insets = useSafeAreaInsets();
    const position = useRef(new Animated.Value(!hideMenuBar ? 0 : 100)).current; // Assuming the height of the component is less than 100

    useEffect(() => {
        console.log(`hideMenuBar: ${hideMenuBar}`)
        Animated.timing(position, {
            toValue: !hideMenuBar ? 0 : 100,
            duration: 200,
            useNativeDriver: true,
        }).start();
    }, [hideMenuBar]);

    const setActive = (menu: MenuType) => {
        dispatch(switchToMenu(menu));
        navigation.navigate(menu);
    };

    const menuBarStyle = useMemo(() => {
        let result = null
        if (isOS(OSType.Android)) {
            result = {
                ...style.menuBar,
                paddingBottom: menuBarPaddingOffset,
            }
        } else {
            result = {
                ...style.menuBar,
                paddingBottom: insets.bottom + menuBarPaddingOffset,
            }
        }
        const menuBarHeight = result.paddingTop + result.paddingBottom + style.icon.height
        dispatch(updateMenuBarHeight(menuBarHeight))
        return result
    }, [hideMenuBar, menuBarPaddingOffset]);

    return (
        <Animated.View
            style={{...menuBarStyle, transform: [{translateY: position}]}}>
            {menu.map((item, i) => (
                <TouchableOpacity
                    activeOpacity={1.0}
                    key={i}
                    style={style.menuItem}
                    onPress={() => setActive(item.type)}>
                    <View>
                        {<item.icon {...kIconSize} opacity={active===item.type ? 1.0 : kInactiveOpacity} />}
                    </View>
                </TouchableOpacity>
            ))}
        </Animated.View>
    );
}
