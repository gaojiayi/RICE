<script setup lang="ts">
import { ref } from "vue";
import { useRouter, RouteParamsRaw } from "vue-router";
import { WFDialog } from "./components";
import demodata from "./wfdata.json";
const router = useRouter();

const handleEdit = (val: RouteParamsRaw) => {
  router.push({ name: "wftaskoperation", params: val });
};

const input_wf_name = ref("");
const wfTaskData = [
  {
    template_id: "wf-notify-pay-001",
    template_name: "提醒支付",
    cron: " 0 5/15 * * * *",
    create_time: "2016-05-02 23:04:24",
    update_time: "2016-07-02 23:04:24",
    template_data: {},
    status: 0
  }
];
const wfvisible = ref(false);
const wfdata = ref({});
const handleShow = (name: String, data: Object) => {
  wfvisible.value = true;
  wfdata.value = demodata;
  console.log(wfvisible.value);
  console.log(wfdata.value);
};
</script>

<template>
  <div class="wf-list-container">
    <div class="wf-list-header">
      <el-input
        v-model="input_wf_name"
        placeholder="请输入工作流任务名称"
        class="input-with-search"
        size="small"
      >
        <template #prepend>
          <el-button
            ><iconify-icon-offline icon="search"></iconify-icon-offline
          ></el-button>
        </template>
      </el-input>
      <el-button>搜索</el-button>
    </div>
    <el-table :data="wfTaskData" stripe style="width: 100%">
      <el-table-column prop="template_id" label="流程模板ID" width="180" />
      <el-table-column prop="template_name" label="流程模板名称" />
      <el-table-column prop="cron" label="cron表达式" />
      <el-table-column prop="create_time" label="创建时间" />
      <el-table-column prop="update_time" label="更新时间" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" align="left">
        <template #default="scope">
          <el-button
            type="text"
            icon="el-icon-edit"
            @click="handleEdit({ template_id: scope.template_id })"
            >编辑
          </el-button>
          <el-button
            type="text"
            icon="el-icon-show"
            @click="handleShow(scope.template_name, scope.template_data)"
            >查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <WFDialog class = "wf-dialog"
      :wfvisible="wfvisible"
      @update:show-w-f-dialog="
        val => {
          wfvisible = val;
        }
      "
      :wfdata="wfdata"
    ></WFDialog>
  </div>
</template>

<style lang="scss" scoped>
.input-with-search {
  width: 20%;
}
.wf-list-container {
  width: 95%;
  .wf-list-header {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 20px;
    .input-with-search {
      width: 20%;
      margin: 0px 20px;
    }

  }
}
</style>
