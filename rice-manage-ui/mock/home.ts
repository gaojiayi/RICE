import { MockMethod } from "vite-plugin-mock";

export default [
  {
    url: "/admin/statistics",
    method: "get",
    response: () => {
      return {
        data: {
          scheduler_num: 0,
          total_schedule_times: 14,
          task_num: 1
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
          start_time: "2022-12-19 23:13:37",
          collectors: [
            {
              address: "127.0.0.1:9500",
              current: true,
              master: false,
              status: 0
            },
            {
              address: "127.0.0.1:9600",
              current: false,
              master: false,
              status: 0
            },
            {
              address: "127.0.0.1:9700",
              current: false,
              master: false,
              status: 0
            }
          ]
        }
      };
    }
  },
  {
    url: "/admin/latest/task/schedule",
    method: "get",
    response: () => {
      return {
        data: [
          {
            trigger_time: "2022-03-12 10:45:30",
            task_name: "订单超时通知",
            app_name: "订单系统",
            processor_address: "10.122.45.10",
            scheduler_address: "100.16.37.21",
            status: 0
          },
          {
            trigger_time: "2022-02-02 07:45:20",
            task_name: "订单支付通知",
            app_name: "订单系统",
            processor_address: "10.122.45.11",
            scheduler_address: "100.16.37.21",
            status: 1
          },
          {
            trigger_time: "2022-01-22 08:45:20",
            task_name: "商品数据同步",
            app_name: "商品系统",
            processor_address: "10.122.45.12",
            scheduler_address: "100.16.37.22",
            status: 2
          },
          {
            trigger_time: "2022-01-22 09:45:20",
            task_name: "商品数据同步",
            app_name: "商品系统",
            processor_address: "10.122.45.13",
            scheduler_address: "100.16.37.23",
            status: 2
          },
          {
            trigger_time: "2022-01-22 11:06:20",
            task_name: "下架商品删除",
            app_name: "商品系统",
            processor_address: "10.122.45.14",
            scheduler_address: "100.16.37.23",
            status: 2
          },
          {
            trigger_time: "2022-01-24 18:16:20",
            task_name: "定时修改数据状态",
            app_name: "检索系统",
            processor_address: "10.122.45.14",
            scheduler_address: "100.16.37.23",
            status: 3
          },
          {
            trigger_time: "2022-02-10 23:08:30",
            task_name: "定时清理数据",
            app_name: "检索系统",
            processor_address: "10.122.45.14",
            scheduler_address: "100.16.37.23",
            status: 4
          }
        ]
      };
    }
  },
  {
    url: "/admin/chart/info",
    method: "get",
    response: () => {
      return {
        data: {
          appProcessorNumRank: [
            {
              appName: "testApp",
              num: 1
            }
          ],
          scheduleNumForWeek: [
            {
              "2022-12-13": 0
            },
            {
              "2022-12-14": 0
            },
            {
              "2022-12-15": 0
            },
            {
              "2022-12-16": 0
            },
            {
              "2022-12-17": 0
            },
            {
              "2022-12-18": 0
            },
            {
              "2022-12-19": 0
            }
          ],
          taskSuccessRate: {
            execption: 5,
            success: 6,
            failed: 3,
            timeout: 0
          }
        }
      };
    }
  }
] as MockMethod[];
