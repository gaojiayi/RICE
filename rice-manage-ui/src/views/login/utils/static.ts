import { computed } from "vue";
import bgDown from "/@/assets/login/bg-down.png";
import bgUp from "/@/assets/login/bg-up.png";
import avatar from "/@/assets/login/avatar.svg?component";
import illustration from "/@/assets/login/illustration.svg?component";

const currentWeek = computed(() => {
  return illustration;
});

export { bgDown, bgUp, avatar, currentWeek };
