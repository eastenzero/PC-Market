import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Warehouse, WarehouseCreateRequest, WarehouseUpdateRequest } from '@/types/warehouse'

export function listWarehouses(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<Warehouse>>('/warehouses', { params })
}

export function getWarehouse(id: number) {
  return http.get<any, Warehouse>(`/warehouses/${id}`)
}

export function createWarehouse(data: WarehouseCreateRequest) {
  return http.post<any, Warehouse>('/warehouses', data)
}

export function updateWarehouse(id: number, data: WarehouseUpdateRequest) {
  return http.put<any, Warehouse>(`/warehouses/${id}`, data)
}

export function deleteWarehouse(id: number) {
  return http.delete<any, void>(`/warehouses/${id}`)
}
