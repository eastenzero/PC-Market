import http from '@/api/http'
import type { PageResult } from '@/types/api'
import type { Customer, CustomerCreateRequest, CustomerUpdateRequest } from '@/types/customer'

export function listCustomers(params: { page: number; size: number; keyword?: string }) {
  return http.get<any, PageResult<Customer>>('/customers', { params })
}

export function getCustomer(id: number) {
  return http.get<any, Customer>(`/customers/${id}`)
}

export function createCustomer(data: CustomerCreateRequest) {
  return http.post<any, Customer>('/customers', data)
}

export function updateCustomer(id: number, data: CustomerUpdateRequest) {
  return http.put<any, Customer>(`/customers/${id}`, data)
}

export function deleteCustomer(id: number) {
  return http.delete<any, void>(`/customers/${id}`)
}
