import { ThemeBasicStyle } from "@global";
import { EmbySite } from "@model/EmbySite";
import { Button, StyleSheet, Text, Touchable, TouchableOpacity, View } from "react-native";
import { Image } from "./Image";
import { Tag } from "./Tag";

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
    avator: {
        width: 100,
        height: 100,
        borderRadius: 50,
    }
})

export interface SiteProps {
    site: EmbySite;
    active?: boolean;
    theme?: ThemeBasicStyle,
    onPress?: () => void;
    onDelete?: (id: string) => void;
}

export function Site({site, theme, onPress, onDelete, active = false}: SiteProps) {
    return (
        <TouchableOpacity onPress={onPress} activeOpacity={1.0}>
        <View style={{...style.card, ...theme, borderColor: active ? 'green' : 'lightgray'}}>
            <View style={style.user}>
            <Image style={style.avator} source={{uri: avatorUrl(site)}} />
            <Text style={{...theme}}>{site.user.User.Name}</Text>
            </View>
            <View style={style.server}>
            <Text style={{...theme}}>{site.name}</Text>
            <Tag color="green">{site.version}</Tag>
            <Tag color="magenta">{site.server.host}</Tag>
            <Text style={{...theme}}>{site.status}</Text>
            <Button title="删除" 
                disabled={active}
                onPress={() => onDelete?.(site.id)} />
            </View>
        </View>
        </TouchableOpacity>
    )
}