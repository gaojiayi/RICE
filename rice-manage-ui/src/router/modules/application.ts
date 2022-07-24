import { findIndex } from 'lodash-unified';
import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const applicationRouter = {
  path: "/application",
  component: Layout,
  redirect: "/application/detail",
  meta: {
    icon: "app-line",
    title: $t("menus.hsapplication"),
    rank: 10
  },
  children: [
    {
      path: "/application/detail",
      name: "appDetail",
      component: () => import("/@/views/application/appdetail/index.vue"),
      meta: {
        title: $t("menus.hsappDetail")
      }
    },
    {
      path: "/application/processor",
      name: "appProcessor",
      component: () => import("/@/views/application/processor/index.vue"),
      meta: {
        title: $t("menus.hsappProcessor")
      }
    }
  ]
};

export default applicationRouter;
