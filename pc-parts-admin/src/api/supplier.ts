import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Supplier, SupplierCreateRequest, SupplierUpdateRequest } from '@/types/supplier'

export function listSuppliers(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<Supplier>>('/suppliers', { params })
}

export function getSupplier(id: number) {
  return http.get<any, Supplier>(`/suppliers/${id}`)
}

export function createSupplier(data: SupplierCreateRequest) {
  return http.post<any, Supplier>('/suppliers', data)
}

export function updateSupplier(id: number, data: SupplierUpdateRequest) {
  return http.put<any, Supplier>(`/suppliers/${id}`, data)
}

export function deleteSupplier(id: number) {
  return http.delete<any, void>(`/suppliers/${id}`)
}
