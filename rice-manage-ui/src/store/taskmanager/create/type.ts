export type TaskCreateType = {
  appId: number;
  taskCode: string;
  taskName: string;
  taskDesc: string;
  taskType: number;
  parameters: string;
  scheduleType: string;
  timeExpression: string;
  executeType: string;
  threads: number;
  taskRetryCount: number;
  instanceRetryCount: number;
};
