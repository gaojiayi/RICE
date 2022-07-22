export type TaskListDataType = {
  id: number;
  app_name: string;
  task_code: string;
  task_name: string;
  task_type: string;
  parameters: string;
  scheduler_server: string;
  schedule_type: string;
  time_expression: string;
  task_retry_count: number;
  next_trigger_time: string;
  create_time: string;
  status: number;
};
export type TaskListQueryParamType = {
  appName: string;
  taskCode: string;
  pageIndex: number;
  pageSize: number;
};

export type TaskListType = {
  query: TaskListQueryParamType;
  data: TaskListDataType[];
  pageTotal:number;
};
