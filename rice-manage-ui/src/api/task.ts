import { http } from "../utils/http";

// ? 表示可选属性  如果有需要定义返回类型
interface ReponseType extends Promise<any> {
  list?: object[];
  pageTotal?: number;
}

export const fetchTaskInfo = (data?: object) : ReponseType=> {
  return http.request("get", "/fetchTaskInfo", { data });
};
