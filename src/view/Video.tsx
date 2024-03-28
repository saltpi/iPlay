import {OSType, isOS} from '@helper/device';
import { AndroidMPVPlayerView } from './mpv/AndroidMPVPlayer';
import { iOSMPVPlayer } from './mpv/iOSMPVPlayer';

export const Video = isOS(OSType.Android) ? AndroidMPVPlayerView : iOSMPVPlayer;
