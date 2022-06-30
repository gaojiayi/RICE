import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const taskInstanceRouter = {
  path: "/taskinstance",
  name: "taskinstance",
  component: Layout,
  redirect: "/taskinstancemanager",
  meta: {
    icon: "home-filled",
    title: $t("menus.hstaskinstance"),
    rank: 2,
  },
  children: [
    {
      path: "/taskinstancemanager",
      name: "taskinstancemanager",
      component: () => import("/@/views/taskinstance/index.vue"),
      meta: {
        title: $t("menus.hstaskinstance"),
      },
    },
  ],
};

export default taskInstanceRouter;
