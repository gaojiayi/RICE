import { createApp } from 'vue';
import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const taskManagerRouter = {
  path: "/wf",
  // name: "task",
  component: Layout,
  redirect: "/wf/task/list",
  meta: {
    icon: "home-filled",
    title: $t("menus.hswfmanager"),
    rank: 10
  },
  children: [
    {
      path: "/wf/task/list",
      name: "wftasklist",
      component: () => import("/@/views/wf/list/index.vue"),
      meta: {
        title: $t("menus.hswftasklist")
      }
    },
    {
      path: "/wf/task/operation",
      name: "wftaskoperation",
      component: () => import("/@/views/wf/create/index.vue"),
      meta: {
        title: $t("menus.hswftaskoperation")
      }
    },
    {
      path: "/wf/task/record",
      name: "wftaskrecord",
      component: () => import("/@/views/wf/record/index.vue"),
      meta: {
        title: $t("menus.hswftaskrecord")
      }
    }
  ]
};

export default taskManagerRouter;
