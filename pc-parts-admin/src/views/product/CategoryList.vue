<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as categoryApi from '@/api/category'
import type { Category, CategoryCreateRequest, CategoryUpdateRequest } from '@/types/category'

const loading = ref(false)
const list = ref<Category[]>([])
const total = ref(0)

const query = reactive<{ page: number; size: number; keyword: string; parentId?: number }>({
  page: 1,
  size: 10,
  keyword: '',
  parentId: undefined,
})

async function fetchList() {
  loading.value = true
  try {
    const res = await categoryApi.listCategories({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      parentId: query.parentId,
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
  query.parentId = undefined
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

const form = reactive<{ id: number | null } & CategoryCreateRequest>({
  id: null,
  parentId: null,
  name: '',
  sortOrder: 0,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

function openCreate() {
  form.id = null
  form.parentId = null
  form.name = ''
  form.sortOrder = 0
  dialogVisible.value = true
}

function openEdit(row: Category) {
  form.id = row.id
  form.parentId = row.parentId
  form.name = row.name
  form.sortOrder = row.sortOrder
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()
  dialogSubmitting.value = true
  try {
    const parentId = form.parentId || undefined
    const sortOrder = form.sortOrder || 0

    if (form.id) {
      const data: CategoryUpdateRequest = {
        parentId,
        name: form.name,
        sortOrder,
      }
      await categoryApi.updateCategory(form.id, data)
      ElMessage.success('更新成功')
    } else {
      const data: CategoryCreateRequest = {
        parentId,
        name: form.name,
        sortOrder,
      }
      await categoryApi.createCategory(data)
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

async function onDelete(row: Category) {
  try {
    await ElMessageBox.confirm(`确认删除分类【${row.name}】？`, '提示', { type: 'warning' })
    await categoryApi.deleteCategory(row.id)
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
        <span class="card-title">分类管理</span>
        <el-button type="primary" size="large" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="分类名称" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item label="父ID">
        <el-input-number v-model="query.parentId" size="large" :controls="false" :min="0" style="width: 160px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="parentId" label="父ID" width="90" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="sortOrder" label="排序" width="90" />
      <el-table-column prop="level" label="层级" width="90" />
      <el-table-column prop="path" label="路径" min-width="240" />
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

  <el-dialog
    v-model="dialogVisible"
    :title="form.id ? '编辑分类' : '新增分类'"
    width="520px"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="父ID" prop="parentId">
        <el-input-number v-model="form.parentId" :controls="false" :min="0" style="width: 100%" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
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


</style>
