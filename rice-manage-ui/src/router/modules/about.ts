import { $t } from "/@/plugins/i18n";
const Layout = () => import("/@/layout/index.vue");

const aboutRouter = {
  path: "/",
  name: "about",
  component: Layout,
  redirect: "/about/index",
  meta: {
    icon: "home-filled",
    title: $t("menus.hsabout"),
    rank: 99
  },
  children: [
    {
      path: "/about/index",
      name: "aboutindex",
      component: () => import("/@/views/about/index.vue"),
      meta: {
        title: $t("menus.hsabout")
      }
    }
  ]
};

export default aboutRouter;
