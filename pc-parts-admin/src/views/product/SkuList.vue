<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as skuApi from '@/api/sku'
import * as productApi from '@/api/product'
import type { Sku, SkuCreateRequest, SkuUpdateRequest } from '@/types/sku'
import type { Product } from '@/types/product'

const loading = ref(false)
const list = ref<Sku[]>([])
const total = ref(0)

const productOptions = ref<Product[]>([])

const query = reactive<{ page: number; size: number; keyword: string; productId?: number }>({
  page: 1,
  size: 10,
  keyword: '',
  productId: undefined,
})

async function fetchProducts() {
  const res = await productApi.listProducts({ page: 1, size: 1000 })
  productOptions.value = res.items
}

async function fetchList() {
  loading.value = true
  try {
    const res = await skuApi.listSkus({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      productId: query.productId,
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
  query.productId = undefined
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
  productId: null as number | null,
  skuCode: '',
  barcode: '',
  name: '',
  specJson: '',
  price: 0,
  cost: 0,
  status: 1,
})

const rules: FormRules = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入SKU名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'change' }],
  cost: [{ required: true, message: '请输入成本', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function openCreate() {
  form.id = null
  form.productId = null
  form.skuCode = ''
  form.barcode = ''
  form.name = ''
  form.specJson = ''
  form.price = 0
  form.cost = 0
  form.status = 1
  dialogVisible.value = true
}

function openEdit(row: Sku) {
  form.id = row.id
  form.productId = row.productId
  form.skuCode = row.skuCode
  form.barcode = row.barcode || ''
  form.name = row.name
  form.specJson = row.specJson || ''
  form.price = Number(row.price || 0)
  form.cost = Number(row.cost || 0)
  form.status = row.status
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()

  if (!form.productId) {
    return
  }

  dialogSubmitting.value = true
  try {
    if (form.id) {
      const data: SkuUpdateRequest = {
        productId: form.productId,
        skuCode: form.skuCode,
        barcode: form.barcode || undefined,
        name: form.name,
        specJson: form.specJson || undefined,
        price: form.price,
        cost: form.cost,
        status: form.status,
      }
      await skuApi.updateSku(form.id, data)
      ElMessage.success('更新成功')
    } else {
      const data: SkuCreateRequest = {
        productId: form.productId,
        skuCode: form.skuCode,
        barcode: form.barcode || undefined,
        name: form.name,
        specJson: form.specJson || undefined,
        price: form.price,
        cost: form.cost,
        status: form.status,
      }
      await skuApi.createSku(data)
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

async function onDelete(row: Sku) {
  try {
    await ElMessageBox.confirm(`确认删除SKU【${row.name}】？`, '提示', { type: 'warning' })
    await skuApi.deleteSku(row.id)
    ElMessage.success('删除成功')

    if (list.value.length === 1 && query.page > 1) {
      query.page -= 1
    }

    fetchList()
  } catch {
  }
}

async function onToggleStatus(row: Sku) {
  const next = row.status === 1 ? 0 : 1
  try {
    await ElMessageBox.confirm(`确认将SKU【${row.name}】设置为${next === 1 ? '启用' : '禁用'}？`, '提示', {
      type: 'warning',
    })
    await skuApi.updateSkuStatus(row.id, next)
    ElMessage.success('操作成功')
    fetchList()
  } catch {
  }
}

onMounted(async () => {
  await fetchProducts()
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">SKU管理</span>
        <el-button type="primary" size="large" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="名称/SKU/条码" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item label="商品">
        <el-select
          v-model="query.productId"
          size="large"
          clearable
          filterable
          style="width: 240px"
          placeholder="全部"
        >
          <el-option v-for="p in productOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品" min-width="180" />
      <el-table-column prop="skuCode" label="SKU编码" width="160" />
      <el-table-column prop="name" label="名称" min-width="180" />
      <el-table-column prop="barcode" label="条码" width="140" />
      <el-table-column prop="price" label="售价" width="110" />
      <el-table-column prop="cost" label="成本" width="110" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="warning" @click="onToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
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

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑SKU' : '新增SKU'" width="720px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="商品" prop="productId">
        <el-select v-model="form.productId" filterable style="width: 100%" placeholder="请选择">
          <el-option v-for="p in productOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="SKU编码" prop="skuCode">
        <el-input v-model="form.skuCode" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="条码" prop="barcode">
        <el-input v-model="form.barcode" placeholder="可选" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="规格JSON" prop="specJson">
        <el-input v-model="form.specJson" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
      <el-form-item label="售价" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="成本" prop="cost">
        <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
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


</style>
