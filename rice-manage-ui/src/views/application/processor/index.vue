<script setup lang="ts">
import { appStore } from "/@/store";
import Card from "/@/views/application/components/Card.vue";
import { ref, onMounted, nextTick } from "vue";
// import dialogForm from "./components/DialogForm.vue";
import { ElMessage, ElMessageBox } from "element-plus";

defineOptions({
  name: "appProcessor"
});
const svg = `
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
      `;

const pageTotal = ref(0);

const productList = ref([]);
const dataLoading = ref(true);

const getCardListData = async () => {
  try {
    const { data } = await appStore.processorStore.GET_ALL_PROCESSOR();
    productList.value = data.list;
    pageTotal.value = data.list.length;
  } catch (e) {
    console.log(e);
  } finally {
    setTimeout(() => {
      dataLoading.value = false;
    }, 500);
  }
};

onMounted(() => {
  getCardListData();
});

const onPageSizeChange = (size: number) => {
  appStore.processorStore.pageSize = size;
};
const onCurrentChange = (current: number) => {
  appStore.processorStore.pageIndex = current;
};
const handleDownItem = product => {
  ElMessageBox.confirm(
    product
      ? `确认隔离处理器${product.address},其所属任务不再被调度.`
      : "",
    "提示",
    {
      type: "warning"
    }
  )
    .then(() => {
      ElMessage({
        type: "success",
        message: "隔离成功"
      });
    })
    .catch(() => {});
};
</script>

<template>
  <div class="main">
    <div class="w-full flex justify-end mb-4">
      <el-input
        style="width: 300px"
        v-model="appStore.processorStore.appName"
        placeholder="请输入应用名称"
        clearable
      >
        <template #suffix>
          <el-icon class="el-input__icon">
            <IconifyIconOffline
              v-show="appStore.processorStore.appName.length === 0"
              icon="search"
            />
          </el-icon>
        </template>
        <el-button type="primary"
          ><iconify-icon-offline icon="search" />搜索</el-button
        >
      </el-input>
    </div>
    <div
      v-loading="dataLoading"
      :element-loading-svg="svg"
      element-loading-svg-view-box="-10, -10, 50, 50"
    >
      <el-empty
        description="暂无数据"
        v-show="
          productList.filter(v =>
            v.app_name
              .toLowerCase()
              .includes(appStore.processorStore.appName.toLowerCase())
          ).length === 0
        "
      />
      <template v-if="pageTotal > 0">
        <el-row :gutter="16">
          <el-col
            v-for="(product, index) in productList.filter(v =>
              v.app_name
                .toLowerCase()
                .includes(appStore.processorStore.appName.toLowerCase())
            )"
            :key="index"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            :xl="4"
          >
            <Card :processor="product" @delete-item="handleDownItem" />
          </el-col>
        </el-row>
        <el-pagination
          class="float-right"
          v-model:currentPage="appStore.processorStore.pageIndex"
          :page-size="appStore.processorStore.pageSize"
          :total="pageTotal"
          :page-sizes="[12, 24, 36]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onPageSizeChange"
          @current-change="onCurrentChange"
        />
      </template>
    </div>
    <!-- <dialogForm v-model:visible="formDialogVisible" :data="formData" /> -->
  </div>
</template>
