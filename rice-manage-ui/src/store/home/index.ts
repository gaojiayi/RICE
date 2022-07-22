import { defineStore } from "pinia";
import { HomeType, OtherCollectorType } from "./type";
import {
  getStatisticsinfo,
  getCollectorInfo,
  getLastestTaskInstance,
  getChartData
} from "/@/api/home";

export const useHomeStore = defineStore({
  id: "home",
  state: (): HomeType => ({
    statistic: {
      task_num: 0,
      total_schedule_times: 0,
      scheduler_num: 0
    },
    current_controller_info: {
      current_address: "",
      isMaster: false,
      start_time: "",
      status: 0,
      other_collectors: []
    },
    task_info_intime: [{
      trigger_time: "",
      task_name: "",
      app_name: "",
      processor_address: "",
      scheduler_address: "",
      status: 0
    }],
    chart_data: {
      schedule_num_for_week: [],
      app_processor_num_rank: [],
      task_success_rate: {
        timeout: 0,
        success: 0,
        failed: 0,
        execption:0
      }
    }
  }),
  actions: {
    async getStatistic() {
      let { data } = await getStatisticsinfo();
      this.statistic = data;
    },
    async getCollectors() {
      let { data } = await getCollectorInfo();
      let otherCollectors: OtherCollectorType[] = [];
      this.current_controller_info.start_time = data.start_time;
      for (let index in data.collectors) {
        if (data.collectors[index].isCurrent) {
          this.current_controller_info.current_address =
            data.collectors[index].address;
          this.current_controller_info.isMaster =
            data.collectors[index].isMaster;
        } else {
          otherCollectors.push({
            isMaster: data.collectors[index].isMaster,
            address: data.collectors[index].address
          });
        }
      }
      this.current_controller_info.other_collectors = otherCollectors;
    },
    async getLastestTaskInstance() {
      let { data } = await getLastestTaskInstance();
      this.task_info_intime = data;
    },
    async getChartInfo() {
      let { data } = await getChartData();
      this.chart_data = data
    }
  }
});
