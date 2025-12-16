export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  roles: string[]
}

export interface MeResponse {
  userId: number
  username: string
  nickname: string
  roles: string[]
}
