import request from './request'
import { getBrowserStorage, loadSession } from '../stores/authSession'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

function unwrapResult(response) {
  const result = response.data

  if (result?.code !== 200) {
    throw new Error(result?.message || '请求失败，请稍后重试')
  }

  return result.data
}

export async function getPublishedKnowledge(params) {
  return unwrapResult(await request.get('/patient/knowledge', { params }))
}

export async function getPublishedKnowledgeCategories() {
  return unwrapResult(await request.get('/patient/knowledge/categories'))
}

export async function getPublishedKnowledgeDetail(id) {
  return unwrapResult(await request.get(`/patient/knowledge/${id}`))
}

export async function getPublishedRecipes() {
  return unwrapResult(await request.get('/patient/recipe'))
}

export async function getPublishedRecipeDetail(id) {
  return unwrapResult(await request.get(`/patient/recipe/${id}`))
}

export async function askAI(question, context = []) {
  return unwrapResult(await request.post('/patient/ai/question', { question, context }, { timeout: 60000 }))
}

export async function askAIStream({ question, context = [], consultationId = null, onChunk, signal }) {
  const { token } = loadSession(getBrowserStorage())
  const response = await fetch(`${API_BASE_URL}/patient/ai/question/stream`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ question, context, consultationId }),
    signal,
  })

  if (!response.ok || !response.body) {
    if (response.status === 401 || response.status === 403) {
      throw new Error('登录状态已失效，请重新登录后再试')
    }
    throw new Error(`AI 问答暂时不可用（HTTP ${response.status || '网络错误'}）`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder()
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    const chunk = decoder.decode(value, { stream: true })
    if (chunk) {
      onChunk?.(chunk)
    }
  }
}

export async function getAIRecommendations(question) {
  return unwrapResult(await request.post('/patient/ai/recommendations', { question }))
}

export async function getDashboardSummary() {
  const summary = unwrapResult(await request.get('/admin/dashboard'))
  return {
    statusDistribution: normalizeDistribution(summary.statusDistribution, 'status'),
    urgencyDistribution: normalizeDistribution(summary.urgencyDistribution, 'urgency'),
    trendLast6Months: normalizeDistribution(summary.trendLast6Months, 'month'),
  }
}

export async function getDashboardTrend(period = 'month') {
  const trend = unwrapResult(await request.get('/admin/dashboard/trend', {
    params: { period },
  }))
  return normalizeDistribution(trend, 'period')
}

function normalizeDistribution(items = [], labelKey) {
  return items.map((item) => ({ label: item[labelKey], value: Number(item.count || 0) }))
}

export async function getAdminContent(resource, params) {
  return unwrapResult(await request.get(`/admin/${resource}`, { params }))
}

export async function createAdminContent(resource, payload) {
  return unwrapResult(await request.post(`/admin/${resource}`, payload))
}

export async function updateAdminContent(resource, id, payload) {
  return unwrapResult(await request.put(`/admin/${resource}/${id}`, payload))
}

export async function deleteAdminContent(resource, id) {
  return unwrapResult(await request.delete(`/admin/${resource}/${id}`))
}

export async function exportConsultations() {
  const response = await request.get('/admin/export/consultations', { responseType: 'blob' })
  return response.data
}
