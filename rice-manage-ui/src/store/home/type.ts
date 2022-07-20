export type StatisticType = {
  task_num: number;
  total_schedule_times: number;
  scheduler_num: number;
};

export type TaskInfoIntimeType = {
  trigger_time: string;
  task_name: string;
  app_name: string;
  processor_address: string;
  scheduler_address: string;
  status: number;
};

export type OtherCollectorType = {
  address: string;
  isMaster: boolean;
};
export type CurrentControllerInfoType = {
  current_address: string;
  isMaster: boolean;
  start_time: string;
  status: number;
  other_collectors?: OtherCollectorType[];
};

export type AppProcessorNumRankType = {
  app_name: string;
  processor_num: number;
};

export type ScheduleNumForWeekType = {
  date: string;
  num: number;
};

export type TaskSuccessRateType = {
  timeout: number;
  failed: number;
  success: number;
  execption: number;
};
export type ChartType = {
  task_success_rate: TaskSuccessRateType;
  app_processor_num_rank: Array<AppProcessorNumRankType>;
  schedule_num_for_week: ScheduleNumForWeekType[];
};
export type HomeType = {
  statistic: StatisticType;
  current_controller_info: CurrentControllerInfoType;
  task_info_intime: TaskInfoIntimeType[];
  chart_data: ChartType;
};
