import { defineStore } from "pinia";
import { getProcessorinfo } from "/@/api/application";
import { ProcessorType } from "./type";

export const useAppProcessorStore = defineStore({
  id: "appProcessor",
  state: (): ProcessorType => ({
    appName: "",
    pageIndex: 0,
    pageSize: 12
  }),
  actions: {
    async GET_ALL_PROCESSOR() {
      return await getProcessorinfo();
    }
  }
});
