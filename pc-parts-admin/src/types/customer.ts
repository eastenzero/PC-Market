export interface Customer {
  id: number
  name: string
  phone: string | null
  email: string | null
  address: string | null
  status: number
}

export interface CustomerCreateRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  status: number
}

export interface CustomerUpdateRequest {
  name: string
  phone?: string
  email?: string
  address?: string
  status: number
}
