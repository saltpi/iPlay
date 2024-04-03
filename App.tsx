import React, {useEffect, useState} from 'react';
import {Provider} from 'react-redux';
import {persistor, store} from '@store';
import {Toast, toastConfig} from '@helper/toast';
import {restoreSiteAsync} from '@store/embySlice';
import { Router } from '@page/router';
import { SafeAreaProvider, initialWindowMetrics, useSafeAreaInsets } from 'react-native-safe-area-context';
import { PersistGate } from 'redux-persist/integration/react';
import { Appearance, useColorScheme } from 'react-native';
import { ColorScheme, updateTheme } from '@store/themeSlice';
import { Colors } from 'react-native/Libraries/NewAppScreen';
import { printException } from '@helper/log';
import { Device } from '@helper/device';
import { PlayerMonitor } from '@view/PlayerMonitor';
import { StatusBarHeight } from '@view/StatusBar';
import { SwitchSiteDialog } from '@view/SwitchSiteDialog';

function App() {
    const [inited, setInited] = useState(false);
    const isDarkMode = useColorScheme() === 'dark';
    const insets = initialWindowMetrics?.insets
    const init = async () => {
        try {
            await Device.init();
            console.log(`window insets: `, insets)
            store.dispatch(restoreSiteAsync());
        } catch (e) {
            console.log(e);
        }
    };

    const updateAppearance = () => {
        store.dispatch(updateTheme(theme => {
            theme.isDarkMode = (
                (theme.colorScheme === ColorScheme.Auto && isDarkMode) ||
                theme.colorScheme === ColorScheme.Dark
            );
            theme.fontColor = isDarkMode ? Colors.light : Colors.dark;
            theme.backgroundColor = isDarkMode ? Colors.darker : Colors.lighter;
            theme.barStyle = isDarkMode ? 'light-content' : 'dark-content';
            theme.statusBarHeight = StatusBarHeight;
            theme.pagePaddingTop = StatusBarHeight + 56;
            if (insets) {
                theme.safeInsets = insets;
            }
            return theme
        }))
    }

    useEffect(() => {
        init().then(() => setInited(true))
            .catch(printException)
    }, []);

    useEffect(() => {
        updateAppearance();
    }, [isDarkMode])

    Appearance.addChangeListener(({colorScheme}) => {
        updateAppearance();
    })

    if (inited) {
        return (
            <Provider store={store}>
                <PersistGate loading={null} persistor={persistor}>
                <SafeAreaProvider>
                <Router />
                </SafeAreaProvider>
                <Toast config={toastConfig} />
                <SwitchSiteDialog />
                <PlayerMonitor />
                </PersistGate>
            </Provider>
        );
    } else {
        return null;
    }
}

export default App;