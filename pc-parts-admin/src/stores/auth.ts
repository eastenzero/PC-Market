import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'
import type { LoginResponse, MeResponse } from '@/types/auth'

interface AuthState {
  token: string
  user: MeResponse | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: '',
    user: null,
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    async login(username: string, password: string) {
      const res: LoginResponse = await authApi.login({ username, password })
      this.token = res.token
      this.user = {
        userId: res.userId,
        username: res.username,
        nickname: res.nickname,
        roles: res.roles,
      }
      return res
    },
    async fetchMe() {
      const res: MeResponse = await authApi.me()
      this.user = res
      return res
    },
    logout() {
      this.token = ''
      this.user = null
    },
  },
  persist: {
    key: 'pc-parts-auth',
    storage: localStorage,
  },
})
