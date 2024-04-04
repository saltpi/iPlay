import { useAppDispatch, useAppSelector } from '@hook/store';
import { toggleSwitchSiteDialog } from '@store/menuSlice';
import { ScrollView, StyleSheet, View } from 'react-native';
import Dialog from 'react-native-dialog';
import { Site } from './Site';
import { removeSite, switchToSiteAsync } from '@store/embySlice';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { Device, OSType, isOS, screenWidth, windowHeight } from '@helper/device';

const style = StyleSheet.create({
    container: {
        maxHeight: "70%",
        maxWidth: "95%",
    },
    list: {
        width: "100%",
        flex: 1,
    }
})

export function SwitchSiteDialog() {
    const dispatch = useAppDispatch()
    const visible = useAppSelector(state => state.menu.showSwitchSiteDialog)
    const sites = useAppSelector(state => state.emby.sites)
    const site = useAppSelector(state => state.emby.site)
    const theme = useAppSelector(selectThemeBasicStyle)
    const maxHeight = windowHeight * 0.65
    const layout = {
        container: {
            minWidth: isOS(OSType.Android) ? null : screenWidth * (Device.isTablet ? 0.5 : 0.75) + 60,
            padding: 0, 
            ...theme
        },
        siteList: {
            minWidth: isOS(OSType.Android) ? null : screenWidth * (Device.isTablet ? 0.5 : 0.75) + 60,
            ...style.list,
            ...theme,
            maxHeight,
        },
        site: {
            minWidth: isOS(OSType.Android) ? null : screenWidth * 0.5
        }
    }
    return (
        <Dialog.Container visible={visible}
            blurStyle={theme}
            contentStyle={layout.container} 
            onBackdropPress={() => dispatch(toggleSwitchSiteDialog())}>
            <Dialog.Description>
            <ScrollView style={layout.siteList}
                showsVerticalScrollIndicator={false}
                showsHorizontalScrollIndicator={false}
                >
                {sites?.map((s, i) => (
                    <Site key={`${s.id}:${i}`} site={s}
                        style={layout.site}
                        active={s.id === site?.id}
                        onPress={() => dispatch(switchToSiteAsync(s.id))}
                        onDelete={() => dispatch(removeSite(s.id))}
                        theme={theme} />
                ))}
            </ScrollView>
            </Dialog.Description>
        </Dialog.Container>
    );
}
