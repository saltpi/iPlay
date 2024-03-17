import { createListenerMiddleware } from '@reduxjs/toolkit'

export const listenerMiddleware = createListenerMiddleware()
export const listener = listenerMiddleware.middleware