export interface Sku {
  id: number
  productId: number
  productName: string | null
  skuCode: string
  barcode: string | null
  name: string
  specJson: string | null
  price: number
  cost: number
  status: number
  createdAt: string
  updatedAt: string
}

export interface SkuCreateRequest {
  productId: number
  skuCode: string
  barcode?: string
  name: string
  specJson?: string
  price: number
  cost: number
  status: number
}

export interface SkuUpdateRequest {
  productId: number
  skuCode: string
  barcode?: string
  name: string
  specJson?: string
  price: number
  cost: number
  status: number
}
