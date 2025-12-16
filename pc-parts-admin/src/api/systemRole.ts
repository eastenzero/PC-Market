import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { SystemRole, SystemRoleCreateRequest, SystemRoleUpdateRequest } from '@/types/systemRole'

export function listSystemRoles(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<SystemRole>>('/system/roles', { params })
}

export function listAllSystemRoles() {
  return http.get<any, SystemRole[]>('/system/roles/all')
}

export function getSystemRole(id: number) {
  return http.get<any, SystemRole>(`/system/roles/${id}`)
}

export function createSystemRole(data: SystemRoleCreateRequest) {
  return http.post<any, SystemRole>('/system/roles', data)
}

export function updateSystemRole(id: number, data: SystemRoleUpdateRequest) {
  return http.put<any, SystemRole>(`/system/roles/${id}`, data)
}

export function deleteSystemRole(id: number) {
  return http.delete<any, void>(`/system/roles/${id}`)
}
