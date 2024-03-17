export interface Logger {
    info: (message?: any, ...optionalParams: any[]) => void;
    warn: (message?: any, ...optionalParams: any[]) => void;
    error: (message?: any, ...optionalParams: any[]) => void;
    trace: (message?: any, ...optionalParams: any[]) => void;
}
export interface Prefixs {
    [key: string]: string|undefined|null
}

export type Options = Prefixs

const DEFAULT_PREFIXS: Prefixs = {
    info: "%cINFO",
    warn: "%cWARN",
    error: "%cERROR",
    trace: "%cTRACE",
}

const DEFAULT_OPTIONS: Options = {
    info: `
    color: #531dab;
    background: #f9f0ff;
    border-color: #d3adf7;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    margin-inline-end: 8px;
    padding-inline: 7px;
    `,
    warn: `
    color: #c41d7f;
    background: #fff0f6;
    border-color: #ffadd2;
    border: 1px solid;
    border-radius: 4px;
    margin-inline-end: 8px;
    padding-inline: 7px;
    `,
    error: `
    color: #d46b08;
    background: #fff7e6;
    border-color: #ffd591;
    border: 1px solid;
    border-radius: 4px;
    margin-inline-end: 8px;
    padding-inline: 7px;
    `,
    trace: `
    color: #d46b08;
    background: #fff7e6;
    border-color: #ffd591;
    border: 1px solid;
    border-radius: 4px;
    margin-inline-end: 8px;
    padding-inline: 7px;
    `
}

const EMPTY_IMPL = (message?: any, ...optionalParams: any[]) => {}

class ConsoleLogger implements Logger {
    prefixs: Prefixs = DEFAULT_PREFIXS
    options: Options = DEFAULT_OPTIONS
    _silent: boolean = false

    _info = console.info.bind(
        this, 
        this.prefixs.info, 
        this.options.info
    )

    get info() {
        return this.silent ? EMPTY_IMPL : this._info
    }

    _warn = console.warn.bind(
        this,
        this.prefixs.warn,
        this.options.warn
    )

    get warn() {
        return this.silent ? EMPTY_IMPL : this._warn
    }

    _error = console.error.bind(
        this,
        this.prefixs.error,
        this.options.error
    )

    get error() {
        return this.silent ? EMPTY_IMPL : this._error
    }

    _trace = console.trace.bind(
        this,
        this.prefixs.trace,
        this.options.trace
    )

    get trace() {
        return this.silent ? EMPTY_IMPL : this._trace
    }

    constructor(prefixs: Prefixs = DEFAULT_PREFIXS, options: Options = DEFAULT_OPTIONS) {
        this.prefixs = prefixs
        this.options = options
    }

    set silent(value: boolean) {
        this._silent = value
    }

    get silent() {
        return this._silent
    }
}

export const logger = new ConsoleLogger()