import { MockMethod } from "vite-plugin-mock";

const taskInfos = {
  list: [
    {
      id: 1,
      app_name: "订单系统",
      task_code: "order_timeout_delete",
      task_name: "超时订单删除任务",
      task_type: "java内置任务",
      parameters: "{}",
      scheduler_server: "168.23.45.122:8900",
      // scheduler_policy: "每10分钟运行",
      schedule_type: "FIXED_FREQUENCY",
      time_expression: "10",
      task_retry_count: 2,
      next_trigger_time: "2022-7-1 13:23:50",
      create_time: "2022-1-1 11:33:40",
      status: 0
    },
    {
      id: 2,
      app_name: "商品系统",
      task_code: "product_serche_sync",
      task_name: "商品同步检索任务",
      task_type: "java内置任务",
      parameters: "{}",
      scheduler_server: "178.123.110.22:8900",
      // scheduler_policy: "cron=0 5,15,25,35,45,55 * * * *",
      schedule_type: "CRON",
      time_expression: "0 5,15,25,35,45,55 * * * *",
      task_retry_count: 2,
      next_trigger_time: "2022-7-1 13:23:50",
      create_time: "2022-1-1 11:33:40",
      log_visible: false,
      status: 1
    },
    {
      id: 3,
      app_name: "检索系统",
      task_code: "expire_product_remove",
      task_name: "过期团单下架",
      task_type: "java内置任务",
      parameters: "{}",
      scheduler_server: "178.123.110.22:8900",
      // scheduler_policy: "cron=0 5,15,25,35,45,55 * * * *",
      schedule_type: "FIXED_DELAY",
      time_expression: "3600",
      task_retry_count: 2,
      next_trigger_time: "2022-7-1 13:23:50",
      create_time: "2022-1-1 11:33:40",
      status: 0
    }
  ],
  pageTotal: 3
};

const taskInstances = {
  list: [
    {
      id: 1,
      taskCode: "product_serche_sync",
      instanceParams: "100-200",
      parentInstanceId: null,
      actualTriggerTime: "2022-07-23: 12:34:16",
      expectedTriggerTime: "2022-07-23: 12:34:16",
      runningTimes: 10,
      taskTrackerAddress: "121.20.34.55",
      type: "",
      result: "{code:200,message:success}",
      finishedTime: "2022-07-23: 12:34:26",
      createTime: "2022-07-23: 12:34:10",
      status: 2
    },
    {
      id: 2,
      taskCode: "product_serche_sync",
      instanceParams: "100-200",
      parentInstanceId: 1,
      actualTriggerTime: "2022-07-23: 12:34:16",
      expectedTriggerTime: "2022-07-23: 12:34:16",
      runningTimes: 10,
      taskTrackerAddress: "121.20.34.55",
      type: "",
      result: "{code:200,message:success}",
      finishedTime: "2022-07-23: 12:34:26",
      createTime: "2022-07-23: 12:34:10",
      status: 2
    }
  ],
  pageTotal: 2
};
export default [
  {
    url: "/task/list",
    method: "get",
    response: () => {
      return taskInfos;
    }
  },
  {
    url: "/task/instance",
    method: "get",
    response: () => {
      return taskInstances;
    }
  },
  {
    url: "/task/child/instance",
    method: "get",
    response: () => {
      return taskInstances;
    }
  }
] as MockMethod[];
