import { useAppProcessorStore } from "./processor/index"
import { useAppListStore } from "./list/index"
import { Pinia } from "pinia";

export const appStoreHook = (store: Pinia) => {
  return {
    processorStore: useAppProcessorStore(store),
    applicationStore: useAppListStore(store)
  };
};
