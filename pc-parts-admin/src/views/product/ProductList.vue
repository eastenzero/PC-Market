<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as productApi from '@/api/product'
import * as brandApi from '@/api/brand'
import * as categoryApi from '@/api/category'
import type { Product, ProductCreateRequest, ProductUpdateRequest } from '@/types/product'
import type { Brand } from '@/types/brand'
import type { Category } from '@/types/category'

const loading = ref(false)
const list = ref<Product[]>([])
const total = ref(0)

const brandOptions = ref<Brand[]>([])
const categoryOptions = ref<Category[]>([])

const query = reactive<{ page: number; size: number; keyword: string; categoryId?: number; brandId?: number }>({
  page: 1,
  size: 10,
  keyword: '',
  categoryId: undefined,
  brandId: undefined,
})

async function fetchOptions() {
  const [brands, categories] = await Promise.all([
    brandApi.listBrands({ page: 1, size: 1000 }),
    categoryApi.listCategories({ page: 1, size: 1000 }),
  ])
  brandOptions.value = brands.items
  categoryOptions.value = categories.items
}

async function fetchList() {
  loading.value = true
  try {
    const res = await productApi.listProducts({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      categoryId: query.categoryId,
      brandId: query.brandId,
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
  query.categoryId = undefined
  query.brandId = undefined
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
  spuCode: '',
  name: '',
  categoryId: null as number | null,
  brandId: null as number | null,
  description: '',
  status: 1,
})

const rules: FormRules = {
  spuCode: [{ required: true, message: '请输入SPU编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

function openCreate() {
  form.id = null
  form.spuCode = ''
  form.name = ''
  form.categoryId = null
  form.brandId = null
  form.description = ''
  form.status = 1
  dialogVisible.value = true
}

function openEdit(row: Product) {
  form.id = row.id
  form.spuCode = row.spuCode
  form.name = row.name
  form.categoryId = row.categoryId
  form.brandId = row.brandId
  form.description = row.description || ''
  form.status = row.status
  dialogVisible.value = true
}

async function onSubmit() {
  await formRef.value?.validate()

  if (!form.categoryId || !form.brandId) {
    return
  }

  dialogSubmitting.value = true
  try {
    if (form.id) {
      const data: ProductUpdateRequest = {
        spuCode: form.spuCode,
        name: form.name,
        categoryId: form.categoryId,
        brandId: form.brandId,
        description: form.description || undefined,
        status: form.status,
      }
      await productApi.updateProduct(form.id, data)
      ElMessage.success('更新成功')
    } else {
      const data: ProductCreateRequest = {
        spuCode: form.spuCode,
        name: form.name,
        categoryId: form.categoryId,
        brandId: form.brandId,
        description: form.description || undefined,
        status: form.status,
      }
      await productApi.createProduct(data)
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

async function onDelete(row: Product) {
  try {
    await ElMessageBox.confirm(`确认删除商品【${row.name}】？`, '提示', { type: 'warning' })
    await productApi.deleteProduct(row.id)
    ElMessage.success('删除成功')

    if (list.value.length === 1 && query.page > 1) {
      query.page -= 1
    }

    fetchList()
  } catch {
  }
}

onMounted(async () => {
  await fetchOptions()
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">商品管理</span>
        <el-button type="primary" size="large" @click="openCreate">新增</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="名称/SPU编码" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select
          v-model="query.categoryId"
          size="large"
          clearable
          filterable
          style="width: 200px"
          placeholder="全部"
        >
          <el-option
            v-for="c in categoryOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="品牌">
        <el-select v-model="query.brandId" size="large" clearable filterable style="width: 200px" placeholder="全部">
          <el-option v-for="b in brandOptions" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="spuCode" label="SPU编码" width="160" />
      <el-table-column prop="name" label="名称" min-width="180" />
      <el-table-column prop="categoryName" label="分类" width="140" />
      <el-table-column prop="brandName" label="品牌" width="140" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
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
    :title="form.id ? '编辑商品' : '新增商品'"
    width="640px"
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="SPU编码" prop="spuCode">
        <el-input v-model="form.spuCode" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" filterable style="width: 100%" placeholder="请选择">
          <el-option
            v-for="c in categoryOptions"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="品牌" prop="brandId">
        <el-select v-model="form.brandId" filterable style="width: 100%" placeholder="请选择">
          <el-option v-for="b in brandOptions" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="可选" />
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
