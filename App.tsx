import React, {useEffect, useState} from 'react';
import {Provider} from 'react-redux';
import {store} from '@store';
import {Toast, toastConfig} from '@helper/toast';
import {restoreSiteAsync} from '@store/embySlice';
import { Router } from '@page/router';

function App() {
    const [inited, setInited] = useState(false);
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
