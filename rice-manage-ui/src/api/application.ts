import { http } from "../utils/http";
import { Page} from "./type"

interface ReponseType extends Promise<any> {
  list?: object[];
  total?: number;
}

interface AppListReponseType extends Promise<any> {
  appList?: object[];
  page?: Page;
}

export const getProcessorinfo = (data?: object): ReponseType => {
  return http.request("get", "/app/fetchProcessorInfo", { data });
};

export const fetchAppInfos = (data?: object): AppListReponseType => {
  return http.request("get", "/app/fetch", { data });
};
