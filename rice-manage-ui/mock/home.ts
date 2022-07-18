import { MockMethod } from "vite-plugin-mock";

export default [
  {
    url: "/admin/statistics",
    method: "get",
    response: () => {
      return {
        data: {
          task_num: 1234,
          total_schedule_times: 5257,
          scheduler_num: 320
        }
      };
    }
  },
  {
    url: "/admin/collector/info",
    method: "get",
    response: () => {
      return {
        data: {
          collectors: [
            {
              address: "150.23.10.1:8080",
              isMaster: true,
              isCurrent: false,
              status: 0
            },
            {
              address: "150.23.10.2:8080",
              isMaster: false,
              isCurrent: true,
              status: 0
            },
            {
              address: "150.23.10.3:8080",
              isMaster: false,
              isCurrent: false,
              status: 0
            },
            {
              address: "150.23.10.4:8080",
              isMaster: false,
              isCurrent: false,
              status: 0
            }
          ],
          start_time: "2022-01-23 12:45:16"
        }
      };
    }
  }
] as MockMethod[];
