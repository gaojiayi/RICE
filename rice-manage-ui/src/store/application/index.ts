import { useAppProcessorStore } from "./processor/index"
import { Pinia } from "pinia";

export const appStoreHook = (store: Pinia) => {
  return {
    processorStore : useAppProcessorStore(store)
  };
};
