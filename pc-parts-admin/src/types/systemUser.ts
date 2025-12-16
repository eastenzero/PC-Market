export interface SystemUser {
  id: number
  username: string
  nickname: string | null
  phone: string | null
  email: string | null
  status: number
  lastLoginAt: string | null
  roleIds: number[]
  roles: string[]
  createdAt: string
  updatedAt: string
}

export interface SystemUserCreateRequest {
  username: string
  password: string
  nickname?: string
  phone?: string
  email?: string
  status: number
  roleIds?: number[]
}

export interface SystemUserUpdateRequest {
  username: string
  password?: string
  nickname?: string
  phone?: string
  email?: string
  status: number
  roleIds?: number[]
}
