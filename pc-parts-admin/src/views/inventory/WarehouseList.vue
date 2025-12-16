<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as warehouseApi from '@/api/warehouse'
import type { Warehouse, WarehouseCreateRequest, WarehouseUpdateRequest } from '@/types/warehouse'

const loading = ref(false)
const list = ref<Warehouse[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await warehouseApi.listWarehouses({
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

const form = reactive<{ id: number | null } & WarehouseCreateRequest>({
  id: null,
  code: '',
  name: '',
  address: '',
  contactName: '',
  contactPhone: '',
  status: 1,
})

const rules: FormRules = {
  code: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function openCreate() {
  form.id = null
  form.code = ''
  form.name = ''
  form.address = ''
  form.contactName = ''
  form.contactPhone = ''
  form.status = 1
  dialogVisible.value = true
}

function openEdit(row: Warehouse) {
  form.id = row.id
  form.code = row.code
  form.name = row.name
  form.address = row.address || ''
  form.contactName = row.contactName || ''
  form.contactPhone = row.contactPhone || ''
  form.status = row.status
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()
  dialogSubmitting.value = true
  try {
    const data: WarehouseUpdateRequest = {
      code: form.code,
      name: form.name,
      address: form.address || undefined,
      contactName: form.contactName || undefined,
      contactPhone: form.contactPhone || undefined,
      status: form.status,
    }

    if (form.id) {
      await warehouseApi.updateWarehouse(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await warehouseApi.createWarehouse(data)
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

async function onDelete(row: Warehouse) {
  try {
    await ElMessageBox.confirm(`确认删除仓库【${row.name}】？`, '提示', { type: 'warning' })
    await warehouseApi.deleteWarehouse(row.id)
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
        <span>仓库管理</span>
        <el-button type="primary" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" placeholder="仓库名称/编码" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查询</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="code" label="编码" width="140" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="140" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
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
    :title="form.id ? '编辑仓库' : '新增仓库'"
    width="640px"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="可选" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input v-model="form.contactName" placeholder="可选" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="form.contactPhone" placeholder="可选" />
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


</style>
