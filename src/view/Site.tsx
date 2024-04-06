import { ThemeBasicStyle } from "@global";
import { EmbySite } from "@model/EmbySite";
import { Button, StyleSheet, Text, TouchableOpacity, View, ViewStyle } from "react-native";
import { Image } from "./Image";
import { Tag } from "./Tag";
import { DEFAULT_AVATOR_URL } from "@helper/image";

export function avatorUrl(site: EmbySite, type: "Primary" = "Primary") {
    const { server: endpoint, user: {User: {Id: id}}} = site
    return `${endpoint.protocol}://${endpoint.host}:${endpoint.port}${endpoint.path}emby/Users/${id}/Images/${type}?height=152&quality=90`
}

const style = StyleSheet.create({
    card: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: 'white',
        borderRadius: 5,
        borderColor: 'lightgray',
        borderWidth: 2,
        padding: 10,
        margin: 10,
    },
    server: {
        flexDirection: 'column',
        flex: 1,
        paddingLeft: 10,
        paddingRight: 10,
    },
    user: {
        flexDirection: 'column',
        alignItems: 'center',
        marginRight: 10,
        marginBottom: 10,
        marginTop: 10,
    }, 
    name: {
        width: 50,
        textAlign: 'center',
        overflow: 'hidden',
        fontWeight: 'bold',
    },
    avator: {
        width: 48,
        height: 48,
        marginBottom: 4,
        borderWidth: 2,
        borderRadius: 24,
        borderColor: 'lightgray',
    },
    serverName: {
        maxWidth: "50%"
    }
})

export interface SiteProps {
    site: EmbySite;
    active?: boolean;
    theme?: ThemeBasicStyle;
    style?: Partial<ViewStyle>;
    onPress?: () => void;
    onDelete?: (id: string) => void;
}

export function Site({style: extraStyle, site, theme, onPress, onDelete, active = false}: SiteProps) {
    return (
        <TouchableOpacity onPress={onPress} activeOpacity={1.0}>
        <View style={{...style.card, ...theme, ...extraStyle, borderColor: active ? 'green' : 'lightgray'}}>
            <View style={style.user}>
                <Image style={style.avator} 
                    source={{uri: avatorUrl(site)}}
                    fallbackImages={[DEFAULT_AVATOR_URL]} />
                <Text style={{...style.name, ...theme}}
                    ellipsizeMode="tail"
                    numberOfLines={1}>
                    {site.user.User.Name}
                </Text>
            </View>
            <View style={style.server}>
                <Text style={{...style.serverName,...theme}}
                    ellipsizeMode="tail"
                    numberOfLines={1}>
                    {site.name}
                </Text>
                <Tag color="green">{site.version}</Tag>
                <Tag color="magenta">{site.server.host}</Tag>
            </View>
            <Button title="删除" 
                disabled={active}
                onPress={() => onDelete?.(site.id)} />
        </View>
        </TouchableOpacity>
    )
}