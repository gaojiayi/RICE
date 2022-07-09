<script setup lang="ts">
import { useRouter } from "vue-router";
import { onMounted, reactive, ref, toRef, toRefs } from "vue";

const router = useRouter();

const form = reactive({
  app_id: undefined,
  task_code: "",
  task_name: "",
  task_desc: "",
  task_type: 0,
  parameters: "",
  schedule_type: "",
  time_expression: "",
  execute_type: "",
  threads: 1,
  task_retry_count: 0,
  instance_retry_count: 0
});

const appid = toRef(form, "app_id");
const app_select_disable = ref(false);
onMounted(() => {
  // 打印appId
  console.log("appid", router.currentRoute.value.params.appid);
  appid.value = {
    value: router.currentRoute.value.params.appid,
    label: router.currentRoute.value.params.appName
  };
  if (appid.value.value != (undefined || null)) {
    app_select_disable.value = true;
  }
  console.log(form)
});

const value = ref<string[]>([]);
const applicationOptionList = [
  {
    value: 111,
    label: "订单系统"
  },
  {
    value: 222,
    label: "营销系统"
  },
  {
    value: 333,
    label: "检索系统"
  }
];
const taskTypeOptionList = [
  {
    value: 0,
    label: "java内置任务"
  },
  {
    value: 1,
    label: "Map任务"
  },
  {
    value: 2,
    label: "shell任务"
  },
  {
    value: 3,
    label: "python任务"
  },
  {
    value: 4,
    label: "MapReduce任务"
  },
  {
    value: 5,
    label: "工作流任务"
  }
];
const scheduleTypeOptionList = [
  {
    value: "CRON",
    label: "CRON"
  },
  {
    value: "FIXED_DELAY",
    label: "FIXED_DELAY"
  },
  {
    value: "FIXED_FREQUENCY",
    label: "FIXED_FREQUENCY"
  }
];
const executeypeOptionList = [
  {
    value: "SINGLE",
    label: "单机执行"
  },
  {
    value: "BROADCAST",
    label: "广播执行"
  }
];

const onSubmit = () => {};
</script>

<template>
  <div class="create-task-container">
    <el-form :model="form" label-width="150px" style="max-width: 660px">
      <el-form-item label="任务名称">
        <el-input v-model="form.task_name" />
      </el-form-item>
      <el-form-item label="任务编码">
        <el-input v-model="form.task_code" />
      </el-form-item>
      <el-form-item label="应用">
        <el-select
          v-model="form.app_id"
          filterable
          default-first-option
          :reserve-keyword="false"
          placeholder="选择所属应用"
          :disabled="app_select_disable"
        >
          <el-option
            v-for="item in applicationOptionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          /> </el-select
      ></el-form-item>
      <el-form-item label="任务描述">
        <el-input
          v-model="form.task_desc"
          maxlength="30"
          placeholder="请输入"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select
          v-model="form.task_type"
          filterable
          default-first-option
          :reserve-keyword="false"
          placeholder="选择任务类型"
        >
          <el-option
            v-for="item in taskTypeOptionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          /> </el-select
      ></el-form-item>
      <el-form-item label="任务参数">
        <el-input
          v-model="form.parameters"
          maxlength="30"
          placeholder="请输入"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="调度类型">
        <el-select
          v-model="form.schedule_type"
          filterable
          default-first-option
          :reserve-keyword="false"
          placeholder="选择任务调度类型"
        >
          <el-option
            v-for="item in scheduleTypeOptionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          /> </el-select
      ></el-form-item>
      <el-form-item label="间隔时间/Cron表达式">
        <el-input v-model="form.task_name" />
      </el-form-item>
      <el-form-item label="执行类型">
        <el-select
          v-model="form.execute_type"
          filterable
          default-first-option
          :reserve-keyword="false"
          placeholder="选择执行类型"
        >
          <el-option
            v-for="item in executeypeOptionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          /> </el-select
      ></el-form-item>
      <el-form-item label="线程数">
        <el-input v-model="form.threads" />
      </el-form-item>
      <el-form-item label="任务最大重试次数">
        <el-input v-model="form.task_retry_count" />
      </el-form-item>
      <el-form-item label="实例最大重试次数">
        <el-input v-model="form.instance_retry_count" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">创建</el-button>
        <el-button>重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style lang="scss" scoped>
.create-task-container {
  // box-sizing: border-box;
  margin: 20px;
  display: flex;
  justify-content: center;
  background-color: #fff;
  .el-form {
    width: 660px;
    padding: 20px;
  }
}
</style>
