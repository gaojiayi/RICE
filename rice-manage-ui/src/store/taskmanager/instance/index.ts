import { defineStore } from "pinia";
import { fetchTaskInstanceInfo, fetchChildTaskInstance } from "/@/api/task";
import { taskInstanceQueryType } from "./type";
export const useTaskInstanceStore = defineStore({
  id: "taskinstance",
  state: (): taskInstanceQueryType => ({
    queryParam: {
      pageIndex: 1,
      pageSize: 10,
      taskCode: ""
    }
  }),
  actions: {
    async FETCH_INSTANCE_BY_CODE() {
      const resp = await fetchTaskInstanceInfo(this.queryParam);
      const instances = resp.list.filter((item, index, array) => {
        if (this.queryParam.taskCode) {
          return item["taskCode"].includes(this.queryParam.taskCode);
        }
        return true;
      });
      return { list: instances, pageTotal: instances.length };
    }
    ,
    async QUERY_CHILD_INSTANCES(parentInstanceId: string) {
      const resp = await fetchChildTaskInstance(parentInstanceId);
      return resp.list;
    }
  }

});
