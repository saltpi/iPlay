import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getConstants() : {
    titleBarHeight: number
  };

  add(a: number, b: number, callback: (value: number) => void) : void;
  setTitle(title: string) : void;
}

export default TurboModuleRegistry.get<Spec>(
  'TitleBar'
) as Spec | null;