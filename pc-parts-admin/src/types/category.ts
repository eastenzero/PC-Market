export interface Category {
  id: number
  parentId: number | null
  name: string
  sortOrder: number
  level: number
  path: string
}

export interface CategoryCreateRequest {
  parentId?: number | null
  name: string
  sortOrder?: number
}

export interface CategoryUpdateRequest {
  parentId?: number | null
  name: string
  sortOrder?: number
}
