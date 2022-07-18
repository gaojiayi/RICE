<script setup lang="ts">
import { ref } from "vue";
import { useRouter, RouteParamsRaw } from "vue-router";
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
    status: 0
  }
];
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
      <el-table-column prop="template_instance_id" label="ID" width="180" />
      <el-table-column prop="template_name" label="流程模板名称" />
      <el-table-column prop="trigger_time" label="触发时间" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="详情" align="left">
        <template #default="scope">
          <el-button
            type="text"
            icon="el-icon-edit"
            @click="handleEdit({ template_id: scope.template_id })"
            >编辑
          </el-button>
        </template>
      </el-table-column>
    </el-table>
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
