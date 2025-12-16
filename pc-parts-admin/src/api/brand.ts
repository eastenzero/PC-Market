import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Brand, BrandCreateRequest, BrandUpdateRequest } from '@/types/brand'

export function listBrands(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<Brand>>('/brands', { params })
}

export function getBrand(id: number) {
  return http.get<any, Brand>(`/brands/${id}`)
}

export function createBrand(data: BrandCreateRequest) {
  return http.post<any, Brand>('/brands', data)
}

export function updateBrand(id: number, data: BrandUpdateRequest) {
  return http.put<any, Brand>(`/brands/${id}`, data)
}

export function deleteBrand(id: number) {
  return http.delete<any, void>(`/brands/${id}`)
}
