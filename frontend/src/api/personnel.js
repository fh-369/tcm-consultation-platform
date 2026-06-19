import request from './request'

function unwrapResult(response) {
  const result = response.data

  if (result?.code !== 200) {
    throw new Error(result?.message || '请求失败，请稍后重试')
  }

  return result.data
}

export async function getPersonnel(resource, params) {
  return unwrapResult(await request.get(`/admin/personnel/${resource}`, { params }))
}

export async function updateAccountEnabled(id, enabled) {
  return unwrapResult(await request.put(`/admin/personnel/accounts/${id}/status`, { enabled }))
}
