<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import * as salesApi from '@/api/salesOrder'
import * as customerApi from '@/api/customer'
import * as warehouseApi from '@/api/warehouse'
import * as skuApi from '@/api/sku'
import type { SalesOrder } from '@/types/salesOrder'
import type { Customer } from '@/types/customer'
import type { Warehouse } from '@/types/warehouse'
import type { Sku } from '@/types/sku'

type OrderItemDraft = {
  skuId: number | null
  price: number
  quantity: number
}

const loading = ref(false)
const list = ref<SalesOrder[]>([])
const total = ref(0)

const customerOptions = ref<Customer[]>([])
const warehouseOptions = ref<Warehouse[]>([])
const skuOptions = ref<Sku[]>([])

const query = reactive<{ page: number; size: number; keyword: string; customerId?: number; status?: number }>({
  page: 1,
  size: 10,
  keyword: '',
  customerId: undefined,
  status: undefined,
})

async function fetchCustomers() {
  const res = await customerApi.listCustomers({ page: 1, size: 1000 })
  customerOptions.value = res.items
}

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
    const res = await salesApi.listSalesOrders({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      customerId: query.customerId,
      status: query.status,
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
  query.customerId = undefined
  query.status = undefined
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

const createDialogVisible = ref(false)
const createSubmitting = ref(false)
const createFormRef = ref<FormInstance>()

const createForm = reactive({
  customerId: null as number | null,
  remark: '',
  items: [] as OrderItemDraft[],
})

const createRules: FormRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
}

const createTotal = computed(() => {
  return createForm.items.reduce((sum, it) => sum + Number(it.price || 0) * Number(it.quantity || 0), 0)
})

function openCreate() {
  createForm.customerId = null
  createForm.remark = ''
  createForm.items = []
  addItem()
  createDialogVisible.value = true
}

function addItem() {
  createForm.items.push({ skuId: null, price: 0, quantity: 1 })
}

function removeItem(index: number) {
  createForm.items.splice(index, 1)
}

async function onCreateSubmit() {
  await createFormRef.value?.validate()

  if (!createForm.customerId) {
    return
  }

  if (createForm.items.length === 0) {
    ElMessage.warning('请至少添加一条明细')
    return
  }

  for (const it of createForm.items) {
    if (!it.skuId) {
      ElMessage.warning('请选择SKU')
      return
    }
    if (!it.quantity || it.quantity < 1) {
      ElMessage.warning('数量至少为1')
      return
    }
    if (it.price === null || it.price === undefined) {
      ElMessage.warning('请输入单价')
      return
    }
  }

  createSubmitting.value = true
  try {
    await salesApi.createSalesOrder({
      customerId: createForm.customerId,
      remark: createForm.remark || undefined,
      items: createForm.items.map((it) => ({
        skuId: it.skuId as number,
        price: it.price,
        quantity: it.quantity,
      })),
    })
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    fetchList()
  } catch (e: any) {
    ElMessage.error(e?.message || '创建失败')
  } finally {
    createSubmitting.value = false
  }
}

const detailDialogVisible = ref(false)
const detailOrder = ref<SalesOrder | null>(null)

function openDetail(row: SalesOrder) {
  detailOrder.value = row
  detailDialogVisible.value = true
}

const payDialogVisible = ref(false)
const paySubmitting = ref(false)
const payFormRef = ref<FormInstance>()

const payForm = reactive({
  id: null as number | null,
  amount: 1,
  payMethod: '',
  remark: '',
})

const payRules: FormRules = {
  amount: [{ required: true, message: '请输入收款金额', trigger: 'change' }],
  payMethod: [{ required: true, message: '请输入收款方式', trigger: 'blur' }],
}

function openPay(row: SalesOrder) {
  const totalAmount = Number(row.totalAmount || 0)
  const paidAmount = Number(row.paidAmount || 0)
  const remain = Math.max(0, totalAmount - paidAmount)

  payForm.id = row.id
  payForm.amount = remain >= 1 ? remain : 1
  payForm.payMethod = ''
  payForm.remark = ''
  payDialogVisible.value = true
}

async function onPaySubmit() {
  await payFormRef.value?.validate()

  if (!payForm.id) {
    return
  }

  paySubmitting.value = true
  try {
    await salesApi.paySalesOrder(payForm.id, {
      amount: payForm.amount,
      payMethod: payForm.payMethod,
      remark: payForm.remark || undefined,
    })
    ElMessage.success('收款成功')
    payDialogVisible.value = false
    fetchList()
  } catch (e: any) {
    ElMessage.error(e?.message || '收款失败')
  } finally {
    paySubmitting.value = false
  }
}

const shipDialogVisible = ref(false)
const shipSubmitting = ref(false)
const shipFormRef = ref<FormInstance>()

const shipForm = reactive({
  id: null as number | null,
  warehouseId: null as number | null,
  remark: '',
})

const shipRules: FormRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
}

function openShip(row: SalesOrder) {
  shipForm.id = row.id
  shipForm.warehouseId = null
  shipForm.remark = ''
  shipDialogVisible.value = true
}

async function onShipSubmit() {
  await shipFormRef.value?.validate()

  if (!shipForm.id || !shipForm.warehouseId) {
    return
  }

  shipSubmitting.value = true
  try {
    await salesApi.shipSalesOrder(shipForm.id, {
      warehouseId: shipForm.warehouseId,
      remark: shipForm.remark || undefined,
    })
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchList()
  } catch (e: any) {
    ElMessage.error(e?.message || '发货失败')
  } finally {
    shipSubmitting.value = false
  }
}

function getStatusLabel(status: number) {
  if (status === 2) return '已发货'
  if (status === 1) return '已收款'
  return '待收款'
}

function getStatusType(status: number) {
  if (status === 2) return 'success'
  if (status === 1) return 'primary'
  return 'warning'
}

function canPay(row: SalesOrder) {
  const totalAmount = Number(row.totalAmount || 0)
  const paidAmount = Number(row.paidAmount || 0)
  return row.status !== 2 && paidAmount < totalAmount
}

function canShip(row: SalesOrder) {
  const totalAmount = Number(row.totalAmount || 0)
  const paidAmount = Number(row.paidAmount || 0)
  return row.status !== 2 && paidAmount >= totalAmount
}

onMounted(async () => {
  await Promise.all([fetchCustomers(), fetchWarehouses(), fetchSkus()])
  fetchList()
})
</script>

<template>
  <el-card shadow="never">
    <template #header>
      <div class="card-header">
        <span class="card-title">销售订单</span>
        <el-button type="primary" size="large" @click="openCreate">新建订单</el-button>
      </div>
    </template>

    <el-form class="app-toolbar" inline>
      <el-form-item label="订单号">
        <el-input v-model="query.keyword" size="large" placeholder="订单号" clearable style="width: 220px" />
      </el-form-item>
      <el-form-item label="客户">
        <el-select
          v-model="query.customerId"
          size="large"
          clearable
          filterable
          style="width: 240px"
          placeholder="全部"
        >
          <el-option v-for="c in customerOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" size="large" clearable style="width: 160px" placeholder="全部">
          <el-option label="待收款" :value="0" />
          <el-option label="已收款" :value="1" />
          <el-option label="已发货" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" @click="onSearch">查询</el-button>
        <el-button size="large" @click="onReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" :loading="loading" border style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="customerName" label="客户" min-width="160" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column prop="paidAmount" label="已收" width="120" />
      <el-table-column prop="orderedAt" label="下单时间" width="180" />
      <el-table-column prop="remark" label="备注" min-width="160" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          <el-button v-if="canPay(row)" link type="warning" @click="openPay(row)">收款</el-button>
          <el-button v-if="canShip(row)" link type="success" @click="openShip(row)">发货</el-button>
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

  <el-dialog v-model="createDialogVisible" title="新建销售订单" width="980px" destroy-on-close>
    <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="90px">
      <el-form-item label="客户" prop="customerId">
        <el-select v-model="createForm.customerId" filterable placeholder="请选择" style="width: 100%">
          <el-option v-for="c in customerOptions" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="可选" />
      </el-form-item>

      <div class="dialog-toolbar">
        <span class="dialog-toolbar-title">明细</span>
        <el-button type="primary" @click="addItem">新增明细</el-button>
      </div>

      <el-table :data="createForm.items" border style="width: 100%">
        <el-table-column label="SKU" min-width="260">
          <template #default="{ row }">
            <el-select v-model="row.skuId" filterable placeholder="请选择" style="width: 100%">
              <el-option
                v-for="s in skuOptions"
                :key="s.id"
                :label="`${s.name} (${s.skuCode})`"
                :value="s.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="160">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="数量" width="140">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="金额" width="160">
          <template #default="{ row }">
            {{ (Number(row.price || 0) * Number(row.quantity || 0)).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ $index }">
            <el-button link type="danger" @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="dialog-total">合计：{{ createTotal.toFixed(2) }}</div>
    </el-form>

    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="createSubmitting" @click="onCreateSubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="detailDialogVisible" title="订单详情" width="980px" destroy-on-close>
    <div v-if="detailOrder" class="detail-info">
      <div>订单号：{{ detailOrder.orderNo }}</div>
      <div>客户：{{ detailOrder.customerName || '-' }}</div>
      <div>状态：{{ getStatusLabel(detailOrder.status) }}</div>
      <div>总金额：{{ Number(detailOrder.totalAmount || 0).toFixed(2) }}</div>
      <div>已收：{{ Number(detailOrder.paidAmount || 0).toFixed(2) }}</div>
    </div>

    <el-table v-if="detailOrder" :data="detailOrder.items" border style="width: 100%; margin-top: 12px">
      <el-table-column prop="skuCode" label="SKU编码" width="160" />
      <el-table-column prop="skuName" label="SKU名称" min-width="180" />
      <el-table-column prop="price" label="单价" width="120" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="shippedQuantity" label="已发" width="100" />
    </el-table>

    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="payDialogVisible" title="订单收款" width="640px" destroy-on-close>
    <el-form ref="payFormRef" :model="payForm" :rules="payRules" label-width="90px">
      <el-form-item label="金额" prop="amount">
        <el-input-number v-model="payForm.amount" :min="1" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="方式" prop="payMethod">
        <el-input v-model="payForm.payMethod" placeholder="例如：现金/转账" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="payForm.remark" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="payDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="paySubmitting" @click="onPaySubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="shipDialogVisible" title="订单发货" width="640px" destroy-on-close>
    <el-form ref="shipFormRef" :model="shipForm" :rules="shipRules" label-width="90px">
      <el-form-item label="仓库" prop="warehouseId">
        <el-select v-model="shipForm.warehouseId" filterable placeholder="请选择" style="width: 100%">
          <el-option v-for="w in warehouseOptions" :key="w.id" :label="w.name" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="shipForm.remark" type="textarea" :rows="3" placeholder="可选" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="shipDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="shipSubmitting" @click="onShipSubmit">确定</el-button>
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

.dialog-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 6px 0 10px;
}

.dialog-toolbar-title {
  font-weight: 600;
}

.dialog-total {
  margin-top: 12px;
  text-align: right;
  font-weight: 700;
}

.detail-info {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 16px;
}
</style>
