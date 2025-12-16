export interface Warehouse {
  id: number
  code: string
  name: string
  address: string | null
  contactName: string | null
  contactPhone: string | null
  status: number
}

export interface WarehouseCreateRequest {
  code: string
  name: string
  address?: string
  contactName?: string
  contactPhone?: string
  status: number
}

export interface WarehouseUpdateRequest {
  code: string
  name: string
  address?: string
  contactName?: string
  contactPhone?: string
  status: number
}
