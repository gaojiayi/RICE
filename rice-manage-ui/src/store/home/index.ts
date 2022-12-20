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
        if (data.collectors[index].current) {
          this.current_controller_info.current_address =
            data.collectors[index].address;
          this.current_controller_info.isMaster =
            data.collectors[index].master;
        } else {
          otherCollectors.push({
            isMaster: data.collectors[index].master,
            address: data.collectors[index].address
          });
        }
      }
      this.current_controller_info.other_collectors = otherCollectors;
    },
    async getLastestTaskInstance() {
      let { data, resp_code } = await getLastestTaskInstance();
      if (resp_code===200) {
        this.task_info_intime = data.instances;
      }

    },
    async getChartInfo() {
      let { data } = await getChartData();
      this.chart_data.task_success_rate = data.taskSuccessRate;
      let appProcessorNumRanks = []
      for (var i in data.appProcessorNumRank) {
        let appProcessorNumRank = {}
        appProcessorNumRank["app_name"] = data.appProcessorNumRank[i]['appName']
        appProcessorNumRank["num"] = data.appProcessorNumRank[i]['num']
        appProcessorNumRanks.push(appProcessorNumRank)
      }
      this.chart_data.app_processor_num_rank = appProcessorNumRanks;

      let scheduleNumForWeeks = [];
      for (var i in data.scheduleNumForWeek) {
        let scheduleNumForWeek = {};
        for (let key in data.scheduleNumForWeek[i]) {
          scheduleNumForWeek["date"] = key;
          scheduleNumForWeek["num"] = data.scheduleNumForWeek[i][key];
        }

        scheduleNumForWeeks.push(scheduleNumForWeek);
      }
      this.chart_data.schedule_num_for_week = scheduleNumForWeeks;
      console.log(this.chart_data)
    }
  }
});
