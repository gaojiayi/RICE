import { http } from "../utils/http";

interface ReponseType extends Promise<any> {
  data?: object;
}


export const getStatisticsinfo = (): ReponseType => {
  return http.request("get", "/admin/statistics");
};

;
export const getCollectorInfo = (): ReponseType => {
  return http.request("get", "/admin/collector/info");
};
