import axios, { type AxiosError, type AxiosInstance, type AxiosResponse } from 'axios'
import { getActivePinia } from 'pinia'
import type { ApiResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth'

const http: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

function tryGetAuthStore() {
  try {
    if (!getActivePinia()) return null
    return useAuthStore()
  } catch {
    return null
  }
}

http.interceptors.request.use((config) => {
  const authStore = tryGetAuthStore()
  if (authStore?.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

http.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const body = response.data
    if (body && typeof body.code === 'number') {
      if (body.code === 0) {
        return body.data
      }

      if (body.code === 401) {
        const authStore = tryGetAuthStore()
        authStore?.logout()
        if (window.location.pathname !== '/login') {
          const redirect = encodeURIComponent(
            window.location.pathname + window.location.search + window.location.hash,
          )
          window.location.replace(`/login?redirect=${redirect}`)
        }
      }

      return Promise.reject(new Error(body.message || 'request_failed'))
    }

    return response.data
  },
  (error: AxiosError<ApiResponse<any>>) => {
    const status = error.response?.status
    const code = error.response?.data?.code
    if (status === 401 || code === 401) {
      const authStore = tryGetAuthStore()
      authStore?.logout()
      if (window.location.pathname !== '/login') {
        const redirect = encodeURIComponent(
          window.location.pathname + window.location.search + window.location.hash,
        )
        window.location.replace(`/login?redirect=${redirect}`)
      }
    }
    return Promise.reject(error)
  },
)

export default http
