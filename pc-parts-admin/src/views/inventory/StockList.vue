<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import * as stockApi from '@/api/stock'
import * as warehouseApi from '@/api/warehouse'
import * as skuApi from '@/api/sku'
import type { Warehouse } from '@/types/warehouse'
import type { Sku } from '@/types/sku'
import type { Stock } from '@/types/stock'

const loading = ref(false)
const list = ref<Stock[]>([])
const total = ref(0)

const warehouseOptions = ref<Warehouse[]>([])
const skuOptions = ref<Sku[]>([])

const query = reactive<{ page: number; size: number; keyword: string; warehouseId?: number; skuId?: number }>({
  page: 1,
  size: 10,
  keyword: '',
  warehouseId: undefined,
  skuId: undefined,
})

async function fetchWarehouses() {
  const res = await warehouseApi.listWarehouses({ page: 1, size: 1000 })
  warehouseOptions.value = res.items
}

async function fetchSkus() {
  const res = await skuApi.listSkus({ page: 1, size: 1000 })
  skuOptions.value = res.items
}

async function fetchList() {
  loading.value = true
  try {
    const res = await stockApi.listStocks({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      warehouseId: query.warehouseId,
      skuId: query.skuId,
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
  query.warehouseId = undefined
  query.skuId = undefined
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

onMounted(async () => {
  await Promise.all([fetchWarehouses(), fetchSkus()])
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">库存查询</span>
        <el-button type="primary" size="large" @click="fetchList">刷新</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" size="large" placeholder="SKU/商品" clearable style="width: 240px" />
      </el-form-item>
      <el-form-item label="仓库">
        <el-select
          v-model="query.warehouseId"
          size="large"
          clearable
          filterable
          style="width: 240px"
          placeholder="全部"
        >
          <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="SKU">
        <el-select
          v-model="query.skuId"
          size="large"
          clearable
          filterable
          style="width: 240px"
          placeholder="全部"
        >
          <el-option
            v-for="s in skuOptions"
            :key="s.id"
            :label="`${s.name} (${s.skuCode})`"
            :value="s.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="warehouseName" label="仓库" width="160" />
      <el-table-column prop="skuCode" label="SKU编码" width="160" />
      <el-table-column prop="skuName" label="SKU名称" min-width="180" />
      <el-table-column prop="productName" label="商品" min-width="180" />
      <el-table-column prop="quantity" label="数量" width="120" />
      <el-table-column prop="updatedAt" label="更新时间" width="180" />
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
