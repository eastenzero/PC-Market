<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as roleApi from '@/api/systemRole'
import type { SystemRole, SystemRoleCreateRequest, SystemRoleUpdateRequest } from '@/types/systemRole'

const loading = ref(false)
const list = ref<SystemRole[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await roleApi.listSystemRoles({
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

const form = reactive<{ id: number | null } & SystemRoleCreateRequest>({
  id: null,
  code: '',
  name: '',
  description: '',
})

const rules: FormRules = {
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

function openCreate() {
  form.id = null
  form.code = ''
  form.name = ''
  form.description = ''
  dialogVisible.value = true
}

function openEdit(row: SystemRole) {
  form.id = row.id
  form.code = row.code
  form.name = row.name
  form.description = row.description || ''
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()

  dialogSubmitting.value = true
  try {
    const data: SystemRoleUpdateRequest = {
      code: form.code,
      name: form.name,
      description: form.description || undefined,
    }

    if (form.id) {
      await roleApi.updateSystemRole(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await roleApi.createSystemRole(data)
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

async function onDelete(row: SystemRole) {
  try {
    await ElMessageBox.confirm(`确认删除角色【${row.name}】？`, '提示', { type: 'warning' })
    await roleApi.deleteSystemRole(row.id)
    ElMessage.success('删除成功')

    if (list.value.length === 1 && query.page > 1) {
      query.page -= 1
    }

    fetchList()
  } catch {
  }
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">角色管理</span>
        <el-button type="primary" size="large" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="角色编码/名称" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="编码" width="180" />
      <el-table-column prop="name" label="名称" width="200" />
      <el-table-column prop="description" label="描述" min-width="240" />
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

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="640px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="可选" />
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
