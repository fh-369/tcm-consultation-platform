import request from './request'

function unwrapResult(response) {
  const result = response.data

  if (result?.code !== 200) {
    throw new Error(result?.message || '请求失败，请稍后重试')
  }

  return result.data
}

export async function loginPatient(credentials) {
  return unwrapResult(await request.post('/auth/login/patient', credentials))
}

export async function loginAdmin(credentials) {
  return unwrapResult(await request.post('/auth/login/admin', credentials))
}

export async function registerPatient(profile) {
  return unwrapResult(await request.post('/auth/register', profile))
}
