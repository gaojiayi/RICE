import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const wfTaskManagerRouter = {
  path: "/task",
  // name: "task",
  component: Layout,
  redirect: "/task/list",
  meta: {
    icon: "log-consule",
    title: $t("menus.hstaskmanager"),
    rank: 20
  },
  children: [
    {
      path: "/task/list",
      name: "taskmanagerlist",
      component: () => import("/@/views/taskmanager/list/index.vue"),
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
