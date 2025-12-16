import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Category, CategoryCreateRequest, CategoryUpdateRequest } from '@/types/category'

export function listCategories(params: {
  page: number
  size: number
  keyword?: string
  parentId?: number
}) {
  return http.get<any, PageResult<Category>>('/categories', { params })
}

export function getCategory(id: number) {
  return http.get<any, Category>(`/categories/${id}`)
}

export function createCategory(data: CategoryCreateRequest) {
  return http.post<any, Category>('/categories', data)
}

export function updateCategory(id: number, data: CategoryUpdateRequest) {
  return http.put<any, Category>(`/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return http.delete<any, void>(`/categories/${id}`)
}
