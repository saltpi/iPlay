import { Linking, StyleSheet, TouchableOpacity, View } from "react-native";
import { BaseImage as Image } from '@view/Image';
import { useMemo } from "react";
import CopyLinkIcon from "@asset/link.svg"
import Clipboard from "@react-native-clipboard/clipboard";
import { Toast } from "@helper/toast";
import { Text } from "react-native-svg";
const iinaIcon = require("@view/player/iina.png");
const nplayerIcon = require("@view/player/nplayer.png");
const vlcIcon = require("@view/player/vlc.png");
const infuseIcon = require("@view/player/infuse.png");
const potplayerIcon = require("@view/player/potplayer.png");
const kmplayerIcon = require("@view/player/kmplayer.png");
const mxplayerIcon = require("@view/player/mxplayer.png");
const mxplayerProIcon = require("@view/player/mxplayerpro.png");

const Players = {
    iina: {
        title: "iina",
        icon: iinaIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`iina://weblink?url=${encodeURI(url)}`)
        }
    },
    nplayer: {
        title: "nPlayer",
        icon: nplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`nplayer-${encodeURI(url)}`)
        }
    },
    vlc: {
        title: "VLC",
        icon: vlcIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`vlc://${encodeURI(url)}`)
        }
    },
    infuse: {
        title: "Infuse",
        icon: infuseIcon,
        // infuse://x-callback-url/play?url=
        action: (url: string, title?: string) => {
            Linking.openURL(`infuse://x-callback-url/play?url=${encodeURI(url)}`)
        }
    },
    portplayer: {
        title: "PortPlayer",
        icon: potplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`portplayer://${encodeURI(url)}`)
        }
    },
    kmplayer: {
        title: "KMPlayer",
        icon: kmplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`kmplayer://${encodeURI(url)}`)
        }
    },
    mxplayer: {
        title: "MXPlayer",
        icon: mxplayerIcon,
        action: (url: string, title: string = new URL(url).pathname) => {
            Linking.openURL(`intent:${encodeURI(url)}#Intent;package=com.mxtech.videoplayer.ad;S.title=${title};end`)
        }
    },
    mxplayerpro: {
        title: "MXPlayer Pro",
        icon: mxplayerProIcon,
        action: (url: string, title: string = new URL(url).pathname) => {
            Linking.openURL(`intent:${encodeURI(url)}#Intent;package=com.mxtech.videoplayer.pro;S.title=${title};end`)
        }
    }
}

const style = StyleSheet.create({
    playerList: {
        display: "flex",
        flexDirection: "row",
        justifyContent: "center",
        alignItems: "center",
        margin: 10,
    },
    icon: {
        width: 42,
        aspectRatio: 1,
        height: 42,
        margin: 2.5,
        overflow: "hidden",
    }
})

export interface ExternalPlayerProps {
    title?: string;
    src: string;
    players?: (keyof typeof Players)[]
}

export function ExternalPlayer({
    title,
    src,
    players = Object.keys(Players) as any
}: ExternalPlayerProps) {
    const playerList = useMemo(() => 
        players?.map(type => Players[type]).map(player => (
            <TouchableOpacity activeOpacity={1.0} key={player.title} onPress={() => player.action(src, title)}>
                <Image key={player.title} style={style.icon} source={player.icon} />
            </TouchableOpacity>
        ))
    , [players])
    const copylinkToClipboard = () => {
        Clipboard.setString(src)
        Toast.show({
            type: "success",
            text1: "Link copied to clipboard",
            text2: src,
        })
    }
    return (
        <View style={style.playerList}>
            {playerList}
            <TouchableOpacity activeOpacity={1.0} onPress={copylinkToClipboard}>
                <CopyLinkIcon style={style.icon} />
            </TouchableOpacity>
        </View>
    );
}