import { http } from "../utils/http";

interface ReponseType extends Promise<any> {
  data?: object;
}

interface LastestTaskInstanceReponseType extends Promise<any> {
  data?: object[];
}

export const getStatisticsinfo = (): ReponseType => {
  return http.request("get", "/home/metrics");
};

;
export const getCollectorInfo = (): ReponseType => {
  return http.request("get", "/home/controller/info");
};

export const getLastestTaskInstance = (): LastestTaskInstanceReponseType => {
  return http.request("get", "/home/latest/task");
};

export const getChartData = (): ReponseType => {
  return http.request("get", "/home/chart");
};
