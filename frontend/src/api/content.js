import request from './request'

function unwrapResult(response) {
  const result = response.data

  if (result?.code !== 200) {
    throw new Error(result?.message || '请求失败，请稍后重试')
  }

  return result.data
}

export async function getPublishedKnowledge() {
  return unwrapResult(await request.get('/patient/knowledge'))
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

export async function askAI(question) {
  return unwrapResult(await request.post('/patient/ai/question', { question }))
}

export async function getDashboardSummary() {
  const summary = unwrapResult(await request.get('/admin/dashboard'))
  return {
    statusDistribution: normalizeDistribution(summary.statusDistribution, 'status'),
    urgencyDistribution: normalizeDistribution(summary.urgencyDistribution, 'urgency'),
    trendLast6Months: normalizeDistribution(summary.trendLast6Months, 'month'),
  }
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
