export type Map<K extends string | number | symbol, T> = {
    [key in K]: T | undefined;
};