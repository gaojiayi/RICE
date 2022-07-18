import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const aboutRouter = {
  path: "/about",
  name: "about",
  component: Layout,
  redirect: "/about/introduce",
  meta: {
    icon: "home-filled",
    title: $t("menus.hsabout"),
    rank: 99
  },
  children: [
    {
      path: "/about/introduce",
      name: "aboutintroduce",
      component: () => import("/@/views/about/index.vue"),
      meta: {
        title: $t("menus.hsintroduce")
      }
    },
    {
      path: "/about/official",
      name: "aboutofficial",
      component: () => import("/@/views/about/index.vue"),
      meta: {
        title: $t("menus.hsofficial")
      }
    },
    {
      path: "/about/doc",
      name: "aboutdoc",
      component: () => import("/@/views/about/index.vue"),
      meta: {
        title: $t("menus.hsdoc")
      }
    }
  ]
};

export default aboutRouter;
