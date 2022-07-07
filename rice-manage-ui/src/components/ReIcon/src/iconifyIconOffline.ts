import { iconType } from './types';
import { h, defineComponent } from "vue";
import { Icon as IconifyIcon, addIcon } from "@iconify/vue/dist/offline";
// https://icon-sets.iconify.design/  icon大全

// element-plus icon
import Check from "@iconify-icons/ep/check";
import HomeFilled from "@iconify-icons/ep/home-filled";
import Setting from "@iconify-icons/ep/setting";
import Lollipop from "@iconify-icons/ep/lollipop";
import RefreshRight from "@iconify-icons/ep/refresh-right";
import ArrowDown from "@iconify-icons/ep/arrow-down";
import CloseBold from "@iconify-icons/ep/close-bold";
import Bell from "@iconify-icons/ep/bell";
import Search from "@iconify-icons/ep/search";
import Plus from "@iconify-icons/ep/plus";

addIcon("check", Check);
addIcon("home-filled", HomeFilled);
addIcon("setting", Setting);
addIcon("lollipop", Lollipop);
addIcon("refresh-right", RefreshRight);
addIcon("arrow-down", ArrowDown);
addIcon("close-bold", CloseBold);
addIcon("bell", Bell);
addIcon("search", Search);
addIcon("plus", Plus);

// remixicon
import ArrowRightSLine from "@iconify-icons/ri/arrow-right-s-line";
import ArrowLeftSLine from "@iconify-icons/ri/arrow-left-s-line";
import LogoutCircleRLine from "@iconify-icons/ri/logout-circle-r-line";
import InformationLine from "@iconify-icons/ri/information-line";
import ArrowUpLine from "@iconify-icons/ri/arrow-up-line";
import ArrowDownLine from "@iconify-icons/ri/arrow-down-line";
import Bookmark from "@iconify-icons/ri/bookmark-2-line";
import User from "@iconify-icons/ri/user-3-fill";
import Lock from "@iconify-icons/ri/lock-fill";
import ListCheck from "@iconify-icons/ri/list-check";
import ComputerLine from "@iconify-icons/ri/computer-line";
import HourLine24 from "@iconify-icons/ri/24-hours-line";
import CloseCircleFill from "@iconify-icons/ri/close-circle-fill";
import AddCircleLine from "@iconify-icons/ri/add-circle-line";
import More2Fill from "@iconify-icons/ri/more-2-fill";

addIcon("arrow-right-s-line", ArrowRightSLine);
addIcon("arrow-left-s-line", ArrowLeftSLine);
addIcon("logout-circle-r-line", LogoutCircleRLine);
addIcon("information-line", InformationLine);
addIcon("arrow-up-line", ArrowUpLine);
addIcon("arrow-down-line", ArrowDownLine);
addIcon("bookmark", Bookmark);
addIcon("user", User);
addIcon("lock", Lock);
addIcon("list-check", ListCheck);
addIcon("computer-line", ComputerLine);
addIcon("24-hour-line", HourLine24);
addIcon("close-circle-fill", CloseCircleFill);
addIcon("add-circle-line", AddCircleLine);
addIcon("more-vertical", More2Fill);

// Iconify Icon在Vue里离线使用（用于内网环境）https://docs.iconify.design/icon-components/vue/offline.html
export default defineComponent({
  name: "IconifyIcon",
  components: { IconifyIcon },
  props: {
    icon: {
      type: String,
      default: ""
    }
  },
  render() {
    const attrs = this.$attrs;
    return h(
      IconifyIcon,
      {
        icon: `${this.icon}`,
        ...attrs
      },
      {
        default: () => []
      }
    );
  }
});
