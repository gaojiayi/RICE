import { useAppStore } from "./../../modules/app";
import { defineStore } from "pinia";
import { fetchAppInfos, createApp, deleteApp } from "/@/api/application";
import { AppListParamType } from "./type";

export const useAppListStore = defineStore({
  id: "appList",
  state: (): AppListParamType => ({
    appName: "",
    pageIndex: 1,
    pageSize: 10
  }),
  actions: {
    async GET_APP_INFOS() {
      return await fetchAppInfos({
        appName: this.appName,
        pageIndex: this.pageIndex,
        pageLimit: this.pageSize
      });
    },
    async CREATE_APP(appName, appDesc) {
      return await createApp({ appName, appDesc });
    },
    async DELETE_APP(appId) {
      return await deleteApp(appId);
    }
  }
});
