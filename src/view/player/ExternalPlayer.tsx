import { Linking, StyleSheet, TouchableOpacity, View } from "react-native";
import { BaseImage as Image } from '@view/Image';
import { useMemo } from "react";
import CopyLinkIcon from "@asset/link.svg"
import Clipboard from "@react-native-clipboard/clipboard";
import { Toast } from "@helper/toast";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { OSType, isOS } from "@helper/device";
import { IntentModule } from "@helper/native";
const iinaIcon = require("@view/player/iina.png");
const nplayerIcon = require("@view/player/nplayer.png");
const vlcIcon = require("@view/player/vlc.png");
const infuseIcon = require("@view/player/infuse.png");
const potplayerIcon = require("@view/player/potplayer.png");
const kmplayerIcon = require("@view/player/kmplayer.png");
const mxplayerIcon = require("@view/player/mxplayer.png");
const mpvIcon = require("@view/player/mpv.png");


const Players = {
    iina: {
        enable: isOS(OSType.macOS),
        title: "iina",
        icon: iinaIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`iina://weblink?url=${encodeURI(url)}`)
        }
    },
    nplayer: {
        enable: isOS(OSType.iOS),
        title: "nPlayer",
        icon: nplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`nplayer-${encodeURI(url)}`)
        }
    },
    vlc: {
        enable: true,
        title: "VLC",
        icon: vlcIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`vlc://${encodeURI(url)}`)
        }
    },
    infuse: {
        enable: isOS(OSType.iOS) || isOS(OSType.macOS),
        title: "Infuse",
        icon: infuseIcon,
        // infuse://x-callback-url/play?url=
        action: (url: string, title?: string) => {
            Linking.openURL(`infuse://x-callback-url/play?url=${encodeURI(url)}`)
        }
    },
    portplayer: {
        enable: isOS(OSType.Windows),
        title: "PortPlayer",
        icon: potplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`portplayer://${encodeURI(url)}`)
        }
    },
    kmplayer: {
        enable: isOS(OSType.Android) || isOS(OSType.iOS),
        title: "KMPlayer",
        icon: kmplayerIcon,
        action: (url: string, title?: string) => {
            Linking.openURL(`kmplayer://${encodeURI(url)}`)
        }
    },
    mxplayer: {
        enable: isOS(OSType.Android),
        title: "MXPlayer",
        icon: mxplayerIcon,
        action: (url: string, title: string = new URL(url).pathname) => {
            if (isOS(OSType.Android)) {
                const deepLink = `intent:${encodeURI(url)}#Intent;package=com.mxtech.videoplayer.ad;S.title=${title};end`
                console.log(deepLink)
                IntentModule.openUrl(deepLink)
            }
        }
    },
    mpv: {
        enable: isOS(OSType.Android),
        title: "mpv-android",
        icon: mpvIcon,
        action: (url: string, title: string = new URL(url).pathname) => {
            if (isOS(OSType.Android)) {
                const urlWithoutScheme = url.replace(/^[a-z]+:\/\//, "")
                const deepLink = `intent://${encodeURI(urlWithoutScheme)}#Intent;type=video/any;package=is.xyz.mpv;scheme=https;end;`
                console.log(deepLink)
                IntentModule.openUrl(deepLink)
            }
        }
    }
}

const style = StyleSheet.create({
    playerList: {
        display: "flex",
        flexDirection: "row",
        flexWrap: "wrap",
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
    },
    copy: {
        margin: 2.5,
        marginLeft: 5,
        maxWidth: 42,
        width: 36,
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
    const insets = useSafeAreaInsets()
    const playerList = useMemo(() => 
        players?.map(type => Players[type])
            .filter(p => p.enable).map(player => (
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
            topOffset: insets.top + 2.5,
        })
    }
    return (
        <View style={style.playerList}>
            {playerList}
            <TouchableOpacity style={style.copy} 
                activeOpacity={1.0}
                onPress={copylinkToClipboard}>
                <CopyLinkIcon width={style.copy.width} />
            </TouchableOpacity>
        </View>
    );
}