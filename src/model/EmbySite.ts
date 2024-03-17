import { EmbyConfig } from '@api/config';
import { User } from '@model/User';

export interface EmbySite {
    server: EmbyConfig;
    user: User;
    status: 'idle' | 'loading' | 'failed';
    disable?: boolean
}
