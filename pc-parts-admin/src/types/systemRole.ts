export interface SystemRole {
  id: number
  code: string
  name: string
  description: string | null
}

export interface SystemRoleCreateRequest {
  code: string
  name: string
  description?: string
}

export interface SystemRoleUpdateRequest {
  code: string
  name: string
  description?: string
}
