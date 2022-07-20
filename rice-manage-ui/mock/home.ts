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
          task_success_rate: {
            timeout: 3,
            failed: 204,
            success: 1079,
            execption: 79
          },
          app_processor_num_rank: [
            {
              app_name: "订单系统",
              processor_num: 10
            },
            {
              app_name: "商品系统",
              processor_num: 15
            },
            {
              app_name: "检索系统",
              processor_num: 8
            },
            {
              app_name: "营销系统",
              processor_num: 5
            }
          ],
          schedule_num_for_week: [
            {
              date: "2022-07-13",
              num: 421
            },
            {
              date: "2022-07-14",
              num: 521
            },
            {
              date: "2022-07-15",
              num: 201
            },
            {
              date: "2022-07-16",
              num: 840
            },
            {
              date: "2022-07-17",
              num: 1200
            },
            {
              date: "2022-07-18",
              num: 612
            },
            {
              date: "2022-07-19",
              num: 752
            }
          ]
        }
      };
    }
  }
] as MockMethod[];
