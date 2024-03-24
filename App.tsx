import React, {useEffect, useState} from 'react';
import {Provider} from 'react-redux';
import {persistor, store} from '@store';
import {Toast, toastConfig} from '@helper/toast';
import {restoreSiteAsync} from '@store/embySlice';
import { Router } from '@page/router';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { PersistGate } from 'redux-persist/integration/react';
import { useColorScheme } from 'react-native';
import { updateTheme } from '@store/themeSlice';
import { Colors } from 'react-native/Libraries/NewAppScreen';

function App() {
    const [inited, setInited] = useState(false);
    const isDarkMode = useColorScheme() === 'dark';
    const init = async () => {
        try {
            store.dispatch(restoreSiteAsync());
        } catch (e) {
            console.log(e);
        }
    };

    useEffect(() => {
        init().then(() => setInited(true));
    }, []);

    useEffect(() => {
        store.dispatch(updateTheme(theme => {
            theme.isDarkMode = isDarkMode;
            theme.fontColor = isDarkMode ? Colors.light : Colors.dark;
            theme.backgroundColor = isDarkMode ? Colors.darker : Colors.lighter;
            theme.barStyle = isDarkMode ? 'light-content' : 'dark-content';
            return theme
        }))
    }, [isDarkMode])

    if (inited) {
        return (
            <Provider store={store}>
                <PersistGate loading={null} persistor={persistor}>
                <SafeAreaProvider>
                <Router />
                </SafeAreaProvider>
                <Toast config={toastConfig} />
                </PersistGate>
            </Provider>
        );
    } else {
        return null;
    }
}

export default App;