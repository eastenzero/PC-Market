export interface Product {
  id: number
  spuCode: string
  name: string
  categoryId: number
  categoryName: string | null
  brandId: number
  brandName: string | null
  description: string | null
  status: number
  createdAt: string
  updatedAt: string
}

export interface ProductCreateRequest {
  spuCode: string
  name: string
  categoryId: number
  brandId: number
  description?: string
  status: number
}

export interface ProductUpdateRequest {
  spuCode: string
  name: string
  categoryId: number
  brandId: number
  description?: string
  status: number
}
