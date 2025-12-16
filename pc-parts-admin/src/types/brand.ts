export interface Brand {
  id: number
  name: string
  description: string
  status: number
}

export interface BrandCreateRequest {
  name: string
  description?: string
  status: number
}

export interface BrandUpdateRequest {
  name: string
  description?: string
  status: number
}
