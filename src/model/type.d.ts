type UnPromisify<T> = T extends Promise<infer U> ? U : T;
type SyncReturnType<T> = UnPromisify<ReturnType<T>>
type nil = undefined|null