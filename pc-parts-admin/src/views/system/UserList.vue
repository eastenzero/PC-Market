<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as userApi from '@/api/systemUser'
import * as roleApi from '@/api/systemRole'
import type { SystemUser, SystemUserCreateRequest, SystemUserUpdateRequest } from '@/types/systemUser'
import type { SystemRole } from '@/types/systemRole'

const loading = ref(false)
const list = ref<SystemUser[]>([])
const total = ref(0)

const roleOptions = ref<SystemRole[]>([])

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
})

async function fetchRoles() {
  const res = await roleApi.listAllSystemRoles()
  roleOptions.value = res
}

async function fetchList() {
  loading.value = true
  try {
    const res = await userApi.listSystemUsers({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
    })
    list.value = res.items
    total.value = Number(res.total || 0)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  query.page = 1
  fetchList()
}

function onReset() {
  query.keyword = ''
  onSearch()
}

function onSizeChange(size: number) {
  query.size = size
  query.page = 1
  fetchList()
}

function onCurrentChange(page: number) {
  query.page = page
  fetchList()
}

const dialogVisible = ref(false)
const dialogSubmitting = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: null as number | null,
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  status: 1,
  roleIds: [] as number[],
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (!form.id && !value) {
          callback(new Error('请输入密码'))
          return
        }
        callback()
      },
      trigger: 'blur',
    },
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function openCreate() {
  form.id = null
  form.username = ''
  form.password = ''
  form.nickname = ''
  form.phone = ''
  form.email = ''
  form.status = 1
  form.roleIds = []
  dialogVisible.value = true
}

function openEdit(row: SystemUser) {
  form.id = row.id
  form.username = row.username
  form.password = ''
  form.nickname = row.nickname || ''
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.status = row.status
  form.roleIds = Array.isArray(row.roleIds) ? row.roleIds : []
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()

  dialogSubmitting.value = true
  try {
    if (form.id) {
      const data: SystemUserUpdateRequest = {
        username: form.username,
        password: form.password || undefined,
        nickname: form.nickname || undefined,
        phone: form.phone || undefined,
        email: form.email || undefined,
        status: form.status,
        roleIds: form.roleIds,
      }
      await userApi.updateSystemUser(form.id, data)
      ElMessage.success('更新成功')
    } else {
      const data: SystemUserCreateRequest = {
        username: form.username,
        password: form.password,
        nickname: form.nickname || undefined,
        phone: form.phone || undefined,
        email: form.email || undefined,
        status: form.status,
        roleIds: form.roleIds,
      }
      await userApi.createSystemUser(data)
      ElMessage.success('新增成功')
    }

    dialogVisible.value = false
    fetchList()
  } catch (e: any) {
    ElMessage.error(e?.message || '提交失败')
  } finally {
    dialogSubmitting.value = false
  }
}

async function onDelete(row: SystemUser) {
  try {
    await ElMessageBox.confirm(`确认删除用户【${row.username}】？`, '提示', { type: 'warning' })
    await userApi.deleteSystemUser(row.id)
    ElMessage.success('删除成功')

    if (list.value.length === 1 && query.page > 1) {
      query.page -= 1
    }

    fetchList()
  } catch {
  }
}

onMounted(async () => {
  await fetchRoles()
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">用户管理</span>
        <el-button type="primary" size="large" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="用户名/昵称/电话/邮箱" clearable style="width: 260px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="nickname" label="昵称" width="160" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column label="角色" min-width="200">
        <template #default="{ row }">
          <template v-if="row.roles && row.roles.length">
            <el-tag v-for="r in row.roles" :key="r" style="margin-right: 6px">{{ r }}</el-tag>
          </template>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginAt" label="最后登录" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="720px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '不修改请留空' : '请输入密码'" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="可选" />
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="可选" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="可选" />
      </el-form-item>
      <el-form-item label="角色" prop="roleIds">
        <el-select v-model="form.roleIds" multiple filterable placeholder="可选" style="width: 100%">
          <el-option v-for="r in roleOptions" :key="r.id" :label="`${r.name} (${r.code})`" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="dialogSubmitting" @click="onSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-weight: 700;
  letter-spacing: -0.2px;
}

.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
