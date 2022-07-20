import { http } from "../utils/http";

interface ReponseType extends Promise<any> {
  data?: object;
}

interface LastestTaskInstanceReponseType extends Promise<any> {
  data?: object[];
}

export const getStatisticsinfo = (): ReponseType => {
  return http.request("get", "/admin/statistics");
};

;
export const getCollectorInfo = (): ReponseType => {
  return http.request("get", "/admin/collector/info");
};

export const getLastestTaskInstance = (): LastestTaskInstanceReponseType => {
  return http.request("get", "/admin/latest/task/schedule");
};

export const getChartData = (): ReponseType => {
  return http.request("get", "/admin/chart/info");
};
