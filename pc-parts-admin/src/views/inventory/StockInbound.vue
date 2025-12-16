<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as stockApi from '@/api/stock'
import * as warehouseApi from '@/api/warehouse'
import * as skuApi from '@/api/sku'
import type { Warehouse } from '@/types/warehouse'
import type { Sku } from '@/types/sku'
import type { Stock } from '@/types/stock'

const submitting = ref(false)

const warehouseOptions = ref<Warehouse[]>([])
const skuOptions = ref<Sku[]>([])

const formRef = ref<FormInstance>()

const form = reactive({
  warehouseId: null as number | null,
  skuId: null as number | null,
  quantity: 1,
  remark: '',
})

const rules: FormRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  skuId: [{ required: true, message: '请选择SKU', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'change' }],
}

const result = ref<Stock | null>(null)

async function fetchWarehouses() {
  const res = await warehouseApi.listWarehouses({ page: 1, size: 1000 })
  warehouseOptions.value = res.items
}

async function fetchSkus() {
  const res = await skuApi.listSkus({ page: 1, size: 1000 })
  skuOptions.value = res.items
}

function onReset() {
  form.warehouseId = null
  form.skuId = null
  form.quantity = 1
  form.remark = ''
  result.value = null
}

async function onSubmit() {
  await formRef.value?.validate()

  if (!form.warehouseId || !form.skuId) {
    return
  }

  submitting.value = true
  try {
    const res = await stockApi.stockIn({
      warehouseId: form.warehouseId,
      skuId: form.skuId,
      quantity: form.quantity,
      remark: form.remark || undefined,
    })
    result.value = res
    ElMessage.success('入库成功')
  } catch (e: any) {
    ElMessage.error(e?.message || '入库失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await Promise.all([fetchWarehouses(), fetchSkus()])
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">入库管理</span>
      </div>
    </template>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" style="max-width: 640px">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="form.warehouseId" filterable placeholder="请选择" style="width: 100%">
          <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="SKU" prop="skuId">
        <el-select v-model="form.skuId" filterable placeholder="请选择" style="width: 100%">
          <el-option v-for="s in skuOptions" :key="s.id" :label="`${s.name} (${s.skuCode})`" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交</el-button>
        <el-button @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-alert
      v-if="result"
      type="success"
      show-icon
      :closable="false"
      :title="`操作成功，当前库存：${result.quantity}`"
      style="max-width: 640px"
    />
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
