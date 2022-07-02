import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const taskInstanceRouter = {
  path: "/taskinstance",
  // name: "taskinstance",
  component: Layout,
  redirect: "/taskinstance/manager",
  meta: {
    icon: "home-filled",
    title: $t("menus.hstaskinstance"),
    rank: 20,
  },
  children: [
    {
      path: "/taskinstance/manager",
      name: "taskinstancemanager",
      component: () => import("/@/views/taskinstance/index.vue"),
      meta: {
        title: $t("menus.hstaskinstance"),
      },
    },
  ],
};

export default taskInstanceRouter;
