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
    info: "",
    warn: "",
    error: "",
    trace: "",
}

const DEFAULT_OPTIONS: Options = {
    info: ``,
    warn: ``,
    error: ``,
    trace: ``
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

export function printException(e: any) {
    logger.info(e)
}