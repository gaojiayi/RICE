import { defineStore } from "pinia";
import { fetchAppInfos } from "/@/api/application";
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
      return await fetchAppInfos(this.state);
    }
  }
});
