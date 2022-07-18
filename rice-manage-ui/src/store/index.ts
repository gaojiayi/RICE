import type { App } from "vue";
import { createPinia } from "pinia";
import { useAppStore } from "./modules/app";
import { useEpThemeStore } from "./modules/epTheme";
import { useMultiTagsStore } from "./modules/multiTags";
import { usePermissionStore } from "./modules/permission";
import { useSettingStore } from "./modules/settings";
import { useUserStore } from "./modules/user";
import { useHomeStore } from "./home/index";
const store = createPinia();

export function setupStore(app: App<Element>) {
  app.use(store);
}

const useAppStoreHook = () => {
  return useAppStore(store);
};

const useEpThemeStoreHook = () => {
  return useEpThemeStore(store);
};

const useMultiTagsStoreHook = () => {
  return useMultiTagsStore(store);
};

const usePermissionStoreHook = () => {
  return usePermissionStore(store);
};

const useSettingStoreHook = () => {
  return useSettingStore(store);
};

const useUserStoreHook = () => {
  return useUserStore(store);
};

const useHomeStoreHook = () => {
  return useHomeStore(store);
};
export {
  useAppStoreHook,
  useEpThemeStoreHook,
  useMultiTagsStoreHook,
  usePermissionStoreHook,
  useSettingStoreHook,
  useUserStoreHook,
  useHomeStoreHook
};
