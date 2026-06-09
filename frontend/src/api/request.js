import axios from 'axios'

import { getBrowserStorage, loadSession } from '../stores/authSession'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const { token } = loadSession(getBrowserStorage())

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default request
