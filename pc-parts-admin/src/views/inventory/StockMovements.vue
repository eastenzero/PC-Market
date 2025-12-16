<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import * as stockApi from '@/api/stock'
import type { InventoryMovement } from '@/types/stock'

const loading = ref(false)
const list = ref<InventoryMovement[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
})

async function fetchList() {
  loading.value = true
  try {
    const res = await stockApi.listStockMovements({ page: query.page, size: query.size })
    list.value = res.items
    total.value = Number(res.total || 0)
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">库存流水</span>
        <el-button type="primary" size="large" @click="fetchList">刷新</el-button>
      </div>
    </template>

    <el-table :data="list" :loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="movedAt" label="时间" width="180" />
      <el-table-column prop="warehouseName" label="仓库" width="160" />
      <el-table-column prop="skuCode" label="SKU编码" width="160" />
      <el-table-column prop="skuName" label="SKU名称" min-width="180" />
      <el-table-column prop="productName" label="商品" min-width="180" />
      <el-table-column label="类型" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.movementType === 1" type="success">入库</el-tag>
          <el-tag v-else-if="row.movementType === 2" type="warning">出库</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="bizType" label="业务类型" width="140" />
      <el-table-column prop="bizNo" label="业务单号" width="160" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="beforeQuantity" label="变更前" width="100" />
      <el-table-column prop="afterQuantity" label="变更后" width="100" />
      <el-table-column prop="remark" label="备注" min-width="160" />
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
