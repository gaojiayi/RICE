import { MockMethod } from "vite-plugin-mock";

export default [
  {
    url: "/app/fetchProcessorInfo",
    method: "get",
    response: () => {
      return {
        data: {
          list: [
            {
              app_name: "订单系统",
              server_status: 0,
              address: "168.127.30.10",
              port: 2345,
              task: [
                { task_code: "order_timeout_delete", task_status: 0 },
                { task_code: "order_timeout_notify", task_status: 0 },
                { task_code: "order_timeout_cancel", task_status: 1 }
              ]
            },
            {
              app_name: "订单系统",
              server_status: 1,
              address: "168.127.30.11",
              port: 2345,
              task: [
                { task_code: "order_timeout_delete", task_status: 1 },
                { task_code: "order_timeout_notify", task_status: 1 }
              ]
            },
            {
              app_name: "商品系统",
              server_status: 1,
              address: "168.127.30.12",
              port: 2345,
              task: [{ task_code: "product_serche_sync", task_status: 1 }]
            }
          ],
          total: 3
        }
      };
    }
  },
  {
    url: "/app/fetch",
    method: "get",
    response: () => {
      return {
        data: {
          appList: [
            {
              appDesc: "testApp",
              appName: "testApp",
              createTime: 1643049256753,
              id: 101,
              status: 1
            }
          ],
          page: {
            pageIndex: 1,
            pageSize: 20,
            total: 1
          }
        }
      };
    }
  }
] as MockMethod[];
