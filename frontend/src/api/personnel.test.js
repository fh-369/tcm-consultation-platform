import { beforeEach, describe, expect, it, vi } from 'vitest'

const request = {
  get: vi.fn(),
  put: vi.fn(),
}

vi.mock('./request', () => ({ default: request }))

describe('personnel management API', () => {
  beforeEach(() => {
    request.get.mockReset()
    request.put.mockReset()
  })

  it('loads patient and doctor personnel pages', async () => {
    request.get.mockResolvedValue({ data: { code: 200, data: { records: [] } } })
    const { getPersonnel } = await import('./personnel')
    const params = { current: 1, size: 10, keyword: '张' }

    await getPersonnel('users', params)
    await getPersonnel('doctors', params)

    expect(request.get).toHaveBeenCalledWith('/admin/personnel/users', { params })
    expect(request.get).toHaveBeenCalledWith('/admin/personnel/doctors', { params })
  })

  it('updates account enabled status', async () => {
    request.put.mockResolvedValue({
      data: { code: 200, data: { id: 8, enabled: false } },
    })
    const { updateAccountEnabled } = await import('./personnel')

    const result = await updateAccountEnabled(8, false)

    expect(request.put).toHaveBeenCalledWith('/admin/personnel/accounts/8/status', {
      enabled: false,
    })
    expect(result.enabled).toBe(false)
  })
})
