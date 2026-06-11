import { beforeEach, describe, expect, it, vi } from 'vitest'

const request = {
  delete: vi.fn(),
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
}

vi.mock('./request', () => ({ default: request }))

describe('content and AI API', () => {
  beforeEach(() => {
    Object.values(request).forEach((mock) => mock.mockReset())
  })

  it('loads public knowledge and recipes', async () => {
    request.get.mockResolvedValue({ data: { code: 200, data: [] } })
    const { getPublishedKnowledge, getPublishedRecipes } = await import('./content')

    await getPublishedKnowledge()
    await getPublishedRecipes()

    expect(request.get).toHaveBeenCalledWith('/patient/knowledge')
    expect(request.get).toHaveBeenCalledWith('/patient/recipe')
  })

  it('asks the patient AI endpoint', async () => {
    request.post.mockResolvedValue({ data: { code: 200, data: { answer: '建议规律作息' } } })
    const { askAI } = await import('./content')

    await askAI('最近睡眠不好怎么办？')

    expect(request.post).toHaveBeenCalledWith('/patient/ai/question', {
      question: '最近睡眠不好怎么办？',
    })
  })

  it('manages admin content through the correct resource path', async () => {
    request.get.mockResolvedValue({ data: { code: 200, data: { records: [] } } })
    request.post.mockResolvedValue({ data: { code: 200, data: { id: 1 } } })
    request.put.mockResolvedValue({ data: { code: 200, data: { id: 1 } } })
    request.delete.mockResolvedValue({ data: { code: 200, data: null } })
    const { createAdminContent, deleteAdminContent, getAdminContent, updateAdminContent } =
      await import('./content')
    const payload = { title: '春季养肝', published: true }

    await getAdminContent('knowledge', { current: 1 })
    await createAdminContent('knowledge', payload)
    await updateAdminContent('knowledge', 1, payload)
    await deleteAdminContent('knowledge', 1)

    expect(request.get).toHaveBeenCalledWith('/admin/knowledge', { params: { current: 1 } })
    expect(request.post).toHaveBeenCalledWith('/admin/knowledge', payload)
    expect(request.put).toHaveBeenCalledWith('/admin/knowledge/1', payload)
    expect(request.delete).toHaveBeenCalledWith('/admin/knowledge/1')
  })

  it('downloads consultation CSV from the plural endpoint', async () => {
    const csv = new Blob(['id,status'])
    request.get.mockResolvedValue({ data: csv })
    const { exportConsultations } = await import('./content')

    const result = await exportConsultations()

    expect(request.get).toHaveBeenCalledWith('/admin/export/consultations', {
      responseType: 'blob',
    })
    expect(result).toBe(csv)
  })

  it('normalizes dashboard SQL aliases for display', async () => {
    request.get.mockResolvedValue({
      data: {
        code: 200,
        data: {
          statusDistribution: [{ status: '待接诊', count: 2 }],
          urgencyDistribution: [{ urgency: '普通', count: 3 }],
          trendLast6Months: [{ month: '2026-06', count: 4 }],
        },
      },
    })
    const { getDashboardSummary } = await import('./content')

    const result = await getDashboardSummary()

    expect(result.statusDistribution).toEqual([{ label: '待接诊', value: 2 }])
    expect(result.urgencyDistribution).toEqual([{ label: '普通', value: 3 }])
    expect(result.trendLast6Months).toEqual([{ label: '2026-06', value: 4 }])
  })
})
