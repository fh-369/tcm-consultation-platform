import { beforeEach, describe, expect, it, vi } from 'vitest'

const request = {
  get: vi.fn(),
  put: vi.fn(),
}

vi.mock('./request', () => ({ default: request }))

describe('admin consultation API', () => {
  beforeEach(() => {
    request.get.mockReset()
    request.put.mockReset()
  })

  it('queries the admin consultation endpoint with filters', async () => {
    request.get.mockResolvedValue({ data: { code: 200, data: { records: [] } } })
    const { getAdminConsultations } = await import('./adminConsultation')
    const params = { current: 1, size: 10, status: '待接诊', keyword: '胃痛' }

    await getAdminConsultations(params)

    expect(request.get).toHaveBeenCalledWith('/admin/consultation', { params })
  })

  it('updates a consultation through the admin endpoint', async () => {
    request.put.mockResolvedValue({ data: { code: 200, data: { id: 9 } } })
    const { updateAdminConsultation } = await import('./adminConsultation')
    const payload = { status: '接诊中', doctorNote: '已查看' }

    await updateAdminConsultation(9, payload)

    expect(request.put).toHaveBeenCalledWith('/admin/consultation/9', payload)
  })
})
