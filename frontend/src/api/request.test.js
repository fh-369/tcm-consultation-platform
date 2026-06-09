import { afterEach, describe, expect, it, vi } from 'vitest'

import request from './request'

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('authenticated request client', () => {
  it('adds a bearer token when a login session exists', async () => {
    vi.stubGlobal('window', {
      localStorage: {
        getItem: () =>
          JSON.stringify({
            token: 'sample-token',
            role: 'patient',
            userId: 7,
            displayName: '林女士',
          }),
      },
    })

    const response = await request.get('/protected-resource', {
      adapter: async (config) => ({
        config,
        data: null,
        headers: {},
        status: 200,
        statusText: 'OK',
      }),
    })

    expect(response.config.headers.Authorization).toBe('Bearer sample-token')
  })

  it('does not add Authorization for a guest request', async () => {
    vi.stubGlobal('window', {
      localStorage: {
        getItem: () => null,
      },
    })

    const response = await request.get('/public-resource', {
      adapter: async (config) => ({
        config,
        data: null,
        headers: {},
        status: 200,
        statusText: 'OK',
      }),
    })

    expect(response.config.headers.Authorization).toBeUndefined()
  })
})
