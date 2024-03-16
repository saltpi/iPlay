export interface EmbyConfig {
    host: string
    protocol: "http"|"https"
    port: number
    path: string
}

export interface Giscus {
    enabled: boolean;
    src: string;
    repo: string;
    repoId: string;
    category: string;
    categoryId: string;
    mapping: string;
    strict: number;
    reactionsEnabled: number;
    emitMetadata: number;
    inputPosition: string;
    theme: string;
    lang: string;
    loading: string;
}

export interface Env {
    emby: EmbyConfig
    tmdb: {
        api_key: string|undefined
    },
    adsense: {
        id: string|nil,
        slot: string|nil
    },
    giscus?: Giscus
}

export const ENV: Env = {
    emby: {
        host: process.env.REACT_APP_EMBY_HOST ?? "127.0.0.1",
        port: Number(process.env.REACT_APP_EMBY_PORT) ?? 443,
        protocol: process.env.REACT_APP_EMBY_PROTOCOL as any ?? "https",
        path: process.env.REACT_APP_EMBY_PATH ?? "/"
    },
    tmdb: {
        api_key: process.env.REACT_APP_TMDB_API_KEY ?? ""
    },
    adsense: {
        id: process.env.REACT_APP_GOOGLE_AD_ID ?? "",
        slot: process.env.REACT_APP_GOOGLE_AD_SLOT ?? ""
    },
    giscus: {
        enabled: process.env.REACT_APP_GISCUS_ENABLED === "true",
        src: process.env.REACT_APP_GISCUS_SRC ?? "@giscus/react",
        repo: process.env.REACT_APP_GISCUS_REPO ?? "ourfor/iplay",
        repoId: process.env.REACT_APP_GISCUS_REPO_ID ?? "R_kgDOKF9yOg",
        category: process.env.REACT_APP_GISCUS_CATEGORY ?? "Announcements",
        categoryId: process.env.REACT_APP_GISCUS_CATEGORY_ID ?? "DIC_kwDOKF9yOs4Cd2rv",
        mapping: process.env.REACT_APP_GISCUS_MAPPING ?? "specific",
        strict: Number(process.env.REACT_APP_GISCUS_STRICT) ?? 0,
        reactionsEnabled: Number(process.env.REACT_APP_GISCUS_REACTIONS_ENABLED) ?? 1,
        emitMetadata: Number(process.env.REACT_APP_GISCUS_EMIT_METADATA) ?? 0,
        inputPosition: process.env.REACT_APP_GISCUS_INPUT_POSITION ?? "top",
        theme: process.env.REACT_APP_GISCUS_THEME ?? "dark",
        lang: process.env.REACT_APP_GISCUS_LANG ?? "zh",
        loading: process.env.REACT_APP_GISCUS_LOADING ?? "lazy"
    }
}