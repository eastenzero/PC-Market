import http from '@/api/http'
import type { LoginRequest, LoginResponse, MeResponse } from '@/types/auth'

export function login(data: LoginRequest) {
  return http.post<any, LoginResponse>('/auth/login', data)
}

export function me() {
  return http.get<any, MeResponse>('/auth/me')
}
