import { http } from "../utils/http";

export const getAsyncRoutes = (params?: object) => {
  // return http.request("get", "/getAsyncRoutes", { params });
  //mock
  return new Promise(function (resolve, reject) {
    let value = {
      code: 0,
      info: [{
        path: "/permission",
        redirect: "/permission/page/index",
        meta: {
          title: "menus.permission",
          icon: "lollipop",
          rank: 7,
          showLink: false
        },
        children: [
          {
            path: "/permission/page/index",
            name: "permissionPage",
            meta: {
              title: "menus.permissionPage"
            }
          },
          {
            path: "/permission/button/index",
            name: "permissionButton",
            meta: {
              title: "menus.permissionButton",
              authority: ["v-admin"]
            }
          }
        ]
      }]
    };
    if (true) {
      resolve(value);
    } else {
      reject("");
    }
  });
};
