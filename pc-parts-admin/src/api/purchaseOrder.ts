import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { PurchaseOrder, PurchaseOrderCreateRequest, PurchaseReceiveRequest } from '@/types/purchaseOrder'

export function listPurchaseOrders(params: {
  page: number
  size: number
  keyword?: string
  supplierId?: number
  status?: number
}) {
  return http.get<any, PageResult<PurchaseOrder>>('/purchase-orders', { params })
}

export function getPurchaseOrder(id: number) {
  return http.get<any, PurchaseOrder>(`/purchase-orders/${id}`)
}

export function createPurchaseOrder(data: PurchaseOrderCreateRequest) {
  return http.post<any, PurchaseOrder>('/purchase-orders', data)
}

export function receivePurchaseOrder(id: number, data: PurchaseReceiveRequest) {
  return http.post<any, PurchaseOrder>(`/purchase-orders/${id}/receive`, data)
}
