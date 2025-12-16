export interface Stock {
  id: number
  warehouseId: number
  warehouseName: string | null
  skuId: number
  skuCode: string | null
  skuName: string | null
  productId: number | null
  productName: string | null
  quantity: number
  updatedAt: string
}

export interface InventoryMovement {
  id: number
  warehouseId: number
  warehouseName: string | null
  skuId: number
  skuCode: string | null
  skuName: string | null
  productId: number | null
  productName: string | null
  movementType: number
  bizType: string | null
  bizId: number | null
  bizNo: string | null
  quantity: number
  beforeQuantity: number
  afterQuantity: number
  remark: string | null
  movedAt: string
}

export interface StockInRequest {
  warehouseId: number
  skuId: number
  quantity: number
  remark?: string
}

export interface StockOutRequest {
  warehouseId: number
  skuId: number
  quantity: number
  remark?: string
}
