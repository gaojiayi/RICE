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
      scheduler_policy: "每10分钟运行",
      task_retry_count: 2,
      next_trigger_time: "2022-7-1 13:23:50",
      create_time: "2022-1-1 11:33:40",
      log_visible: false,
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
      scheduler_policy: "cron=0 5,15,25,35,45,55 * * * *",
      task_retry_count: 2,
      next_trigger_time: "2022-7-1 13:23:50",
      create_time: "2022-1-1 11:33:40",
      log_visible: false,
      status: 1
    }
  ],
  pageTotal: 2
};

export default [
  {
    url: "/fetchTaskInfo",
    method: "get",
    response: () => {
      return taskInfos;
    }
  }
] as MockMethod[];
