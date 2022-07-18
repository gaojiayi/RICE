import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const wfTaskManagerRouter = {
  path: "/task",
  // name: "task",
  component: Layout,
  redirect: "/task/index",
  meta: {
    icon: "home-filled",
    title: $t("menus.hstaskmanager"),
    rank: 10
  },
  children: [
    {
      path: "/task/index",
      name: "taskmanager",
      component: () => import("/@/views/taskmanager/index.vue"),
      meta: {
        title: $t("menus.hstasklist")
      }
    },
    {
      path: "/task/create",
      name: "taskcreate",
      component: () => import("/@/views/taskmanager/create/index.vue"),
      meta: {
        title: $t("menus.hstaskcreate")
      }
    },
    {
      path: "/task/instance/info",
      name: "taskinstanceinfo",
      component: () => import("/@/views/taskmanager/instance/index.vue"),
      meta: {
        title: $t("menus.hstaskinstance")
      }
    }
  ]
};

export default wfTaskManagerRouter;
