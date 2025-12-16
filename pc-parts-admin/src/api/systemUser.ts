import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { SystemUser, SystemUserCreateRequest, SystemUserUpdateRequest } from '@/types/systemUser'

export function listSystemUsers(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<SystemUser>>('/system/users', { params })
}

export function getSystemUser(id: number) {
  return http.get<any, SystemUser>(`/system/users/${id}`)
}

export function createSystemUser(data: SystemUserCreateRequest) {
  return http.post<any, SystemUser>('/system/users', data)
}

export function updateSystemUser(id: number, data: SystemUserUpdateRequest) {
  return http.put<any, SystemUser>(`/system/users/${id}`, data)
}

export function deleteSystemUser(id: number) {
  return http.delete<any, void>(`/system/users/${id}`)
}
