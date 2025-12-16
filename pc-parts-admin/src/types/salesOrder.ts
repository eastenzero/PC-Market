export interface SalesOrderItem {
  id: number
  skuId: number
  skuCode: string | null
  skuName: string | null
  price: number
  quantity: number
  amount: number
  shippedQuantity: number
}

export interface SalesOrder {
  id: number
  orderNo: string
  customerId: number
  customerName: string | null
  status: number
  totalAmount: number
  paidAmount: number
  remark: string | null
  orderedAt: string
  items: SalesOrderItem[]
}

export interface SalesOrderItemRequest {
  skuId: number
  price: number
  quantity: number
}

export interface SalesOrderCreateRequest {
  customerId: number
  remark?: string
  items: SalesOrderItemRequest[]
}

export interface SalesPayRequest {
  amount: number
  payMethod: string
  remark?: string
}

export interface SalesShipRequest {
  warehouseId: number
  remark?: string
}
