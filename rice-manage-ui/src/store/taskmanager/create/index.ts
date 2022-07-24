import { defineStore } from "pinia";
import { TaskCreateType } from "./type";

export const useTaskCreateStore = defineStore({
  id: "",
  state: (): TaskCreateType => ({
    appId: undefined,
    taskCode: "",
    taskName: "",
    taskDesc: "",
    taskType: 0,
    parameters: "",
    scheduleType: "",
    timeExpression: "",
    executeType: "",
    threads: 0,
    taskRetryCount: 0,
    instanceRetryCount: 0
  }),
  actions: {
    async CREATE_TASK() {
      console.log(this.state);
    },
    async RESET() {
      this.taskCode = "";
      this.taskName = "";
      this.taskDesc = "";
      this.taskType = 0;
      this.parameters = "";
      this.scheduleType = "";
      this.timeExpression = "";
      this.executeType = "";
      this.threads = 0;
      this.taskRetryCount = 0;
      this.instanceRetryCount = 0;
    }
  }
});
