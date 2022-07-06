import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const taskInstanceRouter = {
  path: "/taskinstance",
  // name: "taskinstance",
  component: Layout,
  redirect: "/taskinstance/info",
  meta: {
    icon: "home-filled",
    title: $t("menus.hstaskinstance"),
    rank: 20
  },
  children: [
    {
      path: "/taskinstance/info",
      name: "taskinstanceinfo",
      component: () => import("/@/views/taskinstance/index.vue"),
      meta: {
        title: $t("menus.hstaskinstance")
      }
    }
  ]
};

export default taskInstanceRouter;
