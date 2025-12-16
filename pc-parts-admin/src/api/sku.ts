import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Sku, SkuCreateRequest, SkuUpdateRequest } from '@/types/sku'

export function listSkus(params: {
  page: number
  size: number
  keyword?: string
  productId?: number
}) {
  return http.get<any, PageResult<Sku>>('/skus', { params })
}

export function getSku(id: number) {
  return http.get<any, Sku>(`/skus/${id}`)
}

export function createSku(data: SkuCreateRequest) {
  return http.post<any, Sku>('/skus', data)
}

export function updateSku(id: number, data: SkuUpdateRequest) {
  return http.put<any, Sku>(`/skus/${id}`, data)
}

export function updateSkuStatus(id: number, status: number) {
  return http.patch<any, Sku>(`/skus/${id}/status`, { status })
}

export function deleteSku(id: number) {
  return http.delete<any, void>(`/skus/${id}`)
}
