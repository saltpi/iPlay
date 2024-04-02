import { NativeModule, NativeModules } from 'react-native';

export interface FontModuleType extends NativeModule {
    fontList: () => string[];
    fontListAsync: () => Promise<string[]>
    fontFamilyList: () => string[];
    fontFamilyListAsync: () => Promise<string[]>

    // iOS only
    showFontPicker: () => void
}

export const FontModule: FontModuleType = NativeModules.FontModule