export interface PurchaseOrderItem {
  id: number
  skuId: number
  skuCode: string | null
  skuName: string | null
  price: number
  quantity: number
  amount: number
  receivedQuantity: number
}

export interface PurchaseOrder {
  id: number
  orderNo: string
  supplierId: number
  supplierName: string | null
  status: number
  totalAmount: number
  remark: string | null
  orderedAt: string
  items: PurchaseOrderItem[]
}

export interface PurchaseOrderItemRequest {
  skuId: number
  price: number
  quantity: number
}

export interface PurchaseOrderCreateRequest {
  supplierId: number
  remark?: string
  items: PurchaseOrderItemRequest[]
}

export interface PurchaseReceiveRequest {
  warehouseId: number
  remark?: string
}
