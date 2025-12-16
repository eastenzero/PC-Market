import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'

export function listProducts(params: {
  page: number
  size: number
  keyword?: string
  categoryId?: number
  brandId?: number
}) {
  return http.get<any, PageResult<Product>>('/products', { params })
}

export function getProduct(id: number) {
  return http.get<any, Product>(`/products/${id}`)
}

export function createProduct(data: ProductCreateRequest) {
  return http.post<any, Product>('/products', data)
}

export function updateProduct(id: number, data: ProductUpdateRequest) {
  return http.put<any, Product>(`/products/${id}`, data)
}

export function deleteProduct(id: number) {
  return http.delete<any, void>(`/products/${id}`)
}
