import { EmbyConfig } from '@api/config';
import { Navigation } from '@global';
import { OSType, isOS } from '@helper/device';
import { Toast } from '@helper/toast';
import { useAppDispatch, useAppSelector } from '@hook/store';
import { embyUrl } from '@model/EmbySite';
import { useNavigation } from '@react-navigation/native';
import { loginToSiteAsync } from '@store/embySlice';
import {useState} from 'react';
import {Button, SafeAreaView, StyleSheet, Text, TextInput, View} from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

const style = StyleSheet.create({
    inputLine: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingLeft: 5,
    },
    inputLabel: {
        flexShrink: 0,
        minWidth: 50,
        fontWeight: "600",
        fontSize: 16,
    },
    input: {
        flex: 1,
        height: 40,
        margin: 12,
        fontSize: 16,
        borderWidth: 1,
        borderRadius: 5,
        padding: 2.5,
        backgroundColor: "#f0f0f0",
        borderColor: "#e0e0e0",
    },
    loginButton: {
        marginTop: 12,
        marginBottom: 12,
        marginLeft: "auto",
        marginRight: "auto",
        backgroundColor: "black",
        color: "white",
        borderRadius: 5,
        width: "75%"
    }
});

export function Page() {
    const navigation: Navigation = useNavigation()
    const insets = useSafeAreaInsets()
    const emby = useAppSelector(state => state.emby.site)
    const [server, onChangeServer] = useState(embyUrl(emby) ?? '');
    const [username, onChangeUsername] = useState(emby?.user.User.Name ?? '');
    const [password, onChangePassword] = useState('');
    const [loading, setLoading] = useState(false);
    const dispatch = useAppDispatch()
    const backgroundColor = useAppSelector(state => state.theme.backgroundColor);
    const color = useAppSelector(state => state.theme.fontColor);
    const onLoginPress = async () => {
        const regex = /(?<protocol>http|https):\/\/(?<host>[^\/\:]+):?(?<port>\d+)?(?<path>\/?.*)/
        const groups = server.match(regex)?.groups
        console.log(groups)
        const protocol = groups?.protocol === "https" ? "https" : "http"
        const endpoint: EmbyConfig = {
            host: groups?.host ?? "",
            port: groups?.port ? parseInt(groups.port) : (protocol === "https" ? 443 : 80),
            protocol,
            path: groups?.path ? (groups?.path.length === 0 ? "/" : groups?.path) : "/"
        }
        console.log(`endpoint: `, endpoint)
        const callback = {
            resolve: () => {
                setLoading(false)
                setTimeout(() => {
                    navigation.goBack()
                }, 1000)
                console.log("login success")
                Toast.show({
                    type: 'success',
                    text1: '登录成功',
                    position: 'top',
                    topOffset: insets.top + 2.5, 
                });
            },
            reject: () => {
                setTimeout(() => {
                    setLoading(false)
                }, 1000)
                console.log("login failed")
                Toast.show({
                    type: 'error',
                    text1: '登录失败',
                    text2: '请检查服务器地址、用户名和密码',
                    position: 'top',
                    topOffset: insets.top, 
                });
            }
        }
        setLoading(true)
        dispatch(loginToSiteAsync({
            endpoint,
            username: username?.trim(), 
            password: password?.trim(), 
            callback
        }))
    }
    return (
        <SafeAreaView style={{flex: 1, backgroundColor}}>
            <View style={style.inputLine}>
                <Text style={style.inputLabel}>服务器</Text>
                <TextInput
                    placeholder="https://server.emby.media"
                    style={{...style.input, color, backgroundColor}}
                    onChangeText={onChangeServer}
                    value={server}
                />
            </View>
            <View style={style.inputLine}>
                <Text style={{...style.inputLabel, color}}>用户名</Text>
                <TextInput
                    placeholder="guest"
                    style={{...style.input, color, backgroundColor}}
                    onChangeText={onChangeUsername}
                    value={username}
                />
            </View>
            <View style={style.inputLine}>
                <Text style={style.inputLabel}>密码</Text>
                <TextInput
                    placeholder="password"
                    style={{...style.input, color, backgroundColor}}
                    onChangeText={onChangePassword}
                    value={password}
                />
            </View>
            <View style={style.loginButton}>
                <Button title={loading ? "登录中..." : "登录"} 
                    color={isOS(OSType.Android) ? "black" : "white"}
                    onPress={onLoginPress} />
            </View>
        </SafeAreaView>
    );
}
