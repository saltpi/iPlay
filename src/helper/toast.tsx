import { StyleSheet, Text, View} from 'react-native';
import { Image } from '@view/Image';
import ToastOrigin, {
    BaseToast,
    ErrorToast,
    ToastConfig,
} from 'react-native-toast-message';

import MessageIcon from '@asset/message.svg';
import { OSType, isOS } from './device';

const style = StyleSheet.create({
    success: {
        borderLeftColor: 'transparent',
        height: 'auto',
        padding: 5,
    },
    icon: {
        width: 20,
        aspectRatio: 1,
        objectFit: 'contain',
    },
});

const toastConfig: ToastConfig = {
    /*
      Overwrite 'success' type,
      by modifying the existing `BaseToast` component
    */
    success: props => (
        <BaseToast
            {...props}
            style={style.success}
            contentContainerStyle={{paddingHorizontal: 15}}
            renderLeadingIcon={() => (
                <MessageIcon
                    width={style.icon.width}
                    style={style.icon}
                />
            )}
            text1Style={{
                fontSize: 15,
                fontWeight: '400',
            }}
        />
    ),
    /*
      Overwrite 'error' type,
      by modifying the existing `ErrorToast` component
    */
    error: props => (
        <ErrorToast
            {...props}
            style={style.success}
            contentContainerStyle={{paddingHorizontal: 15}}
            renderLeadingIcon={() => (
                <MessageIcon
                    width={style.icon.width}
                    style={style.icon}
                />
            )}
            text1Style={{
                fontSize: 15,
                fontWeight: '400',
            }}
        />
    ),
    /*
      Or create a completely new type - `tomatoToast`,
      building the layout from scratch.
  
      I can consume any custom `props` I want.
      They will be passed when calling the `show` method (see below)
    */
    tomatoToast: ({text1, props}) => (
        <View style={{height: 60, width: '100%', backgroundColor: 'tomato'}}>
            <Text>{text1}</Text>
            <Text>{props.uuid}</Text>
        </View>
    ),
};

export function EmptyToast(props: any) {
    return <></>
};

EmptyToast.show = (params: any) => {}
EmptyToast.hide = () => {}

export declare namespace EmptyToast {
    var show: (params: any) => void;
    var hide: (params?: void | undefined) => void;
}

const Toast = isOS(OSType.Windows) ? EmptyToast : ToastOrigin;

export {
    Toast,
    toastConfig,
}