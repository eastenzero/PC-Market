import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { InventoryMovement, Stock, StockInRequest, StockOutRequest } from '@/types/stock'

export function listStocks(params: {
  page: number
  size: number
  warehouseId?: number
  skuId?: number
  keyword?: string
}) {
  return http.get<any, PageResult<Stock>>('/stocks', { params })
}

export function stockIn(data: StockInRequest) {
  return http.post<any, Stock>('/stock/in', data)
}

export function stockOut(data: StockOutRequest) {
  return http.post<any, Stock>('/stock/out', data)
}

export function listStockMovements(params: { page: number; size: number }) {
  return http.get<any, PageResult<InventoryMovement>>('/stock/movements', { params })
}
