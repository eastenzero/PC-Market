import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { SalesOrder, SalesOrderCreateRequest, SalesPayRequest, SalesShipRequest } from '@/types/salesOrder'

export function listSalesOrders(params: {
  page: number
  size: number
  keyword?: string
  customerId?: number
  status?: number
}) {
  return http.get<any, PageResult<SalesOrder>>('/sales-orders', { params })
}

export function getSalesOrder(id: number) {
  return http.get<any, SalesOrder>(`/sales-orders/${id}`)
}

export function createSalesOrder(data: SalesOrderCreateRequest) {
  return http.post<any, SalesOrder>('/sales-orders', data)
}

export function paySalesOrder(id: number, data: SalesPayRequest) {
  return http.post<any, SalesOrder>(`/sales-orders/${id}/pay`, data)
}

export function shipSalesOrder(id: number, data: SalesShipRequest) {
  return http.post<any, SalesOrder>(`/sales-orders/${id}/ship`, data)
}
