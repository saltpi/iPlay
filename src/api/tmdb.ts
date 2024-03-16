import { logger } from "../helper/log";
import { config as GlobalConfig } from "../api/config"

export namespace TMDB {
    const apiHost = 'https://proxyall.endemy.me/3';
    export const imgHost = 'https://image.tmdb.org/t/p/original';
    const config = {
        api_key: GlobalConfig.tmdb.api_key,
        include_adult: false,
        language: 'zh-CN',
        _api: apiHost,
    }

    export interface MovieDetail {
        adult: boolean,
        backdrop_path: string,
        belongs_to_collection: null,
        budget: number,
        genres: {
            id: number,
            name: string
        }[],
        homepage: string,
        id: number,
        imdb_id: string,
        original_language: string,
        original_title: string,
        overview: string,
        popularity: number,
        poster_path: string,
        production_companies: {
            id: number,
            logo_path: string,
            name: string,
            origin_country: string
        }[],
        production_countries: {
            iso_3166_1: string,
            name: string
        }[],
        release_date: string,
        revenue: number,
        runtime: number,
        spoken_languages: {
            english_name: string,
            iso_639_1: string,
            name: string
        }[],
        status: string,
        tagline: string,
        title: string,
        video: boolean,
        vote_average: number,
        vote_count: number
    }
    export interface SearchResponse {
        page: number,
        results: Partial<MovieDetail>[],
        total_pages: number,
        total_results: number
    }

    export async function searchMovie(query: string, page: number = 1): Promise<SearchResponse | null>{
        const options = {
            method: 'GET',
            headers: {
                accept: 'application/json',
                "X-HOST": "api.themoviedb.org"
            }
        };

        const url = new URL(`${config._api}/search/movie`)
        url.searchParams.append('query', query);
        url.searchParams.append('page', page.toString());
        Object.entries(config).forEach(([key, value]) => {
            if (key.startsWith('_') || !value) {
                return;
            }
            url.searchParams.append(key, value?.toString());
        })
        try {
            const response = await fetch(url.toString(), options);
            const data = await response.json();
            return data;
        } catch (err) {
            logger.error(err);
        }
        return null
    }

    // @see https://developer.themoviedb.org/reference/discover-tv
    export async function discoverTV() {
        const url = new URL(`${config._api}/discover/tv`)
        const params = {
            include_adult: false,
            include_video: false,
            language: "zh-CN",
            page: 1,
            sort_by: "popularity.desc"
        }
        Object.entries(params).forEach(([key, value]) => {
            url.searchParams.append(key, value?.toString());
        });
        const options = {
            method: 'GET',
            headers: {
                accept: 'application/json',
                "X-HOST": "api.themoviedb.org"
            }
        };
        const response = await fetch(url, options)
        const data = await response.json() as SearchResponse;
        return data;
    } 

    // @see https://developer.themoviedb.org/reference/discover-movie
    export async function discoverMovie() {
        const url = new URL(`${config._api}/discover/movie`)
        const params = {
            include_adult: false,
            include_video: false,
            language: "zh-CN",
            page: 1,
            sort_by: "popularity.desc"
        }
        Object.entries(params).forEach(([key, value]) => {
            url.searchParams.append(key, value?.toString());
        });
        const options = {
            method: 'GET',
            headers: {
                accept: 'application/json',
                "X-HOST": "api.themoviedb.org"
            }
        };
        const response = await fetch(url, options)
        const data = await response.json() as SearchResponse;
        return data;
    }
}
