import { http } from "../utils/http";
import { Page } from "./type";

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

export const fetchAppInfos = (params?: object): AppListReponseType => {
  return http.request("get", "/app/fetch", { params });
};

export const createApp = (data?: object): object => {
  return http.request("post", "/app/create", {
    data,
    headers: { "Content-Type": "application/json;charset=utf-8" }
  });
};

export const deleteApp = (appId?: string) => {
  return http.request("post", "/app/delete", {
    data: { appId },
    headers: { "Content-Type": "application/json;charset=utf-8" }
  });
};
