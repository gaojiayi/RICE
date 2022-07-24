<script setup lang="ts">
import { useRouter } from "vue-router";
import { onMounted, reactive, ref, toRef, toRefs } from "vue";
import { useTaskManagerHook } from "/@/store";
import { ElMessage } from "element-plus";

const router = useRouter();
const taskCreateStore = useTaskManagerHook().taskCreate;
const appSelectLoading = ref(false);
const appOptions = ref<any[]>([]);

const appSelectDisable = ref(false);
onMounted(() => {
  const currentApp = router.currentRoute.value.params;
  if (currentApp.appid) {
    appOptions.value = [
      {
        value: Number(router.currentRoute.value.params.appid),
        label: router.currentRoute.value.params.appName
      }
    ];
    taskCreateStore.appId = Number(router.currentRoute.value.params.appid);
    appSelectDisable.value = true;
  }
});

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
  },
  {
    value: 444,
    label: "商品系统"
  }
];
const queryApps = (appName: string) => {
  if (appName) {
    appSelectLoading.value = true;
    setTimeout(() => {
      appSelectLoading.value = false;
      appOptions.value = applicationOptionList.filter(item => {
        return item.label.toLowerCase().includes(appName.toLowerCase());
      });
    }, 200);
  } else {
    appOptions.value = [];
  }
};
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
    label: "固定延迟"
  },
  {
    value: "FIXED_FREQUENCY",
    label: "固定频率"
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

const onSubmit = () => {
  // validate
  taskCreateStore
    .CREATE_TASK()
    .then(() => {
      ElMessage.success("创建任务成功");
      reset();
    })
    .catch(() => {});
};
const reset = () => {
  taskCreateStore.RESET()
};
</script>

<template>
  <div class="create-task-container">
    <el-form
      :model="taskCreateStore"
      label-width="150px"
      style="max-width: 660px"
    >
      <el-form-item label="任务名称">
        <el-input v-model="taskCreateStore.taskName" />
      </el-form-item>
      <el-form-item label="任务编码">
        <el-input v-model="taskCreateStore.taskCode" />
      </el-form-item>
      <el-form-item label="应用">
        <el-select
          v-model="taskCreateStore.appId"
          filterable
          remote
          default-first-option
          placeholder="输入所属应用"
          :disabled="appSelectDisable"
          :remote-method="queryApps"
        >
          <el-option
            v-for="item in appOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          /> </el-select
      ></el-form-item>
      <el-form-item label="任务描述">
        <el-input
          v-model="taskCreateStore.taskDesc"
          maxlength="30"
          placeholder="请输入"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="任务类型">
        <el-select
          v-model="taskCreateStore.taskType"
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
          v-model="taskCreateStore.parameters"
          maxlength="30"
          placeholder="请输入"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="调度类型">
        <el-select
          v-model="taskCreateStore.scheduleType"
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
        <el-input v-model="taskCreateStore.timeExpression" />
      </el-form-item>
      <el-form-item label="执行类型">
        <el-select
          v-model="taskCreateStore.executeType"
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
        <el-input v-model="taskCreateStore.threads" />
      </el-form-item>
      <el-form-item label="任务最大重试次数">
        <el-input v-model="taskCreateStore.taskRetryCount" />
      </el-form-item>
      <el-form-item label="实例最大重试次数">
        <el-input v-model="taskCreateStore.instanceRetryCount" />
      </el-form-item>
      <el-form-item >
        <el-button type="primary" @click="onSubmit()">创建</el-button>
        <el-button @click="reset()">重置</el-button>
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
