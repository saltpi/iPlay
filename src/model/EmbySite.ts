import { EmbyConfig } from '@api/config';
import { User } from '@model/User';

export interface EmbySite {
    id: string;
    name?: string;
    version?: string;
    server: EmbyConfig;
    user: User;
    status: 'idle' | 'loading' | 'failed';
    disable?: boolean
}

export function embyUrl(site?: EmbySite|null) {
    if (!site) return null
    return `${site.server.protocol}://${site.server.host}:${site.server.port}${site.server.path}`
}