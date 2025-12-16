export interface Supplier {
  id: number
  name: string
  phone: string | null
  email: string | null
  address: string | null
  status: number
}

export interface SupplierCreateRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  status: number
}

export interface SupplierUpdateRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  status: number
}
