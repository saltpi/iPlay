import { useAppDispatch, useAppSelector } from '@hook/store';
import { toggleSwitchSiteDialog } from '@store/menuSlice';
import { ScrollView, StyleSheet, View } from 'react-native';
import Dialog from 'react-native-dialog';
import { Site } from './Site';
import { removeSite, switchToSiteAsync } from '@store/embySlice';
import { selectThemeBasicStyle } from '@store/themeSlice';
import { windowHeight } from '@helper/device';

const style = StyleSheet.create({
    container: {
        maxHeight: "70%",
        maxWidth: "95%",
    },
    list: {
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
    return (
        <Dialog.Container visible={visible}
            contentStyle={{padding: 0, ...theme}} 
            onBackdropPress={() => dispatch(toggleSwitchSiteDialog())}>
            <Dialog.Description>
            <ScrollView style={{...style.list, maxHeight}}
                showsVerticalScrollIndicator={false}
                showsHorizontalScrollIndicator={false}
                >
                {sites?.map((s, i) => (
                    <Site key={`${s.id}:${i}`} site={s}
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
