import { NativeModules } from 'react-native';

export interface FontModuleType {
    fontList: () => string[];
    fontListAsync: () => Promise<string[]>
    fontFamilyList: () => string[];
    fontFamilyListAsync: () => Promise<string[]>
}

export const FontModule: FontModuleType = NativeModules.FontModule