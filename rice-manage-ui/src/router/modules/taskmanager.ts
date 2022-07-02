import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const taskManagerRouter = {
  path: "/task",
  // name: "task",
  component: Layout,
  redirect: "/task/index",
  meta: {
    icon: "home-filled",
    title: $t("menus.hstaskmanager"),
    rank: 10,
  },
  children: [
    {
      path: "/task/index",
      name: "taskmanager",
      component: () => import("/@/views/taskmanager/index.vue"),
      meta: {
        title: $t("menus.hstaskmanager"),
      },
    },
  ],
};

export default taskManagerRouter;
