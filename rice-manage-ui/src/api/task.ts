import { http } from "../utils/http";

// ? 表示可选属性  如果有需要定义返回类型
interface ReponseType extends Promise<any> {
  list?: object[];
  pageTotal?: number;
}

export const fetchTaskInfo = (data?: object): ReponseType => {
  return http.request("get", "/task/list", { data });
};

export const fetchTaskInstanceInfo = (query?: object): ReponseType => {
  return http.request("get", "/task/instance", query);
};

export const fetchChildTaskInstance = (parentId?: string): ReponseType => {
  return http.request("get", "/task/child/instance", { params: parentId });
};
