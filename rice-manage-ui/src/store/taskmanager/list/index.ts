import { defineStore } from "pinia";
import { TaskListType } from "./type";
import { fetchTaskInfo } from "/@/api/task";
export const useTaskListStore = defineStore({
  id: "task_manager_list",
  state: (): TaskListType => ({
    query: { appName: "", taskCode: "", pageIndex: 1, pageSize: 10 },
    pageTotal: 0,
    data: []
  }),
  getters: {},
  actions: {
    FETCH_ALL_TASK() {
      fetchTaskInfo(this.query).then(res => {
        const tasks = res.list;
        let filterTasks = tasks.filter((item, index, array) => {
          let appNameMatch = true;
          let taskCodeMatch = true;
          if (this.query.appName) {
            appNameMatch = item.app_name.indexOf(this.query.appName) >= 0;
          }
          if (this.query.taskCode) {
            taskCodeMatch = item.task_code.indexOf(this.query.taskCode) >= 0;
          }
          return appNameMatch && taskCodeMatch;
        });

        for (let index in filterTasks) {
          let task = filterTasks[index];
          let schedulePolicy = "";
          if (task.schedule_type == "CRON") {
            schedulePolicy = "CRON=" + task.time_expression;
          }
          if (task.schedule_type == "FIXED_DELAY") {
            schedulePolicy =
              "间隔上次执行" + task.time_expression + "秒后再执行";
          }
          if (task.schedule_type == "FIXED_FREQUENCY") {
            schedulePolicy = "每" + task.time_expression + "秒后再执行";
          }
          task["scheduler_policy"] = schedulePolicy;
        }
        this.data = filterTasks;
        this.pageTotal = filterTasks.length || 0;
      });
    }
  }
});
