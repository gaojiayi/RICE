<script setup lang="ts">
import { ref, unref, onMounted } from "vue";
import { templateRef } from "@vueuse/core";
import { LogicFlow } from "@logicflow/core";
interface Props {
  lf: LogicFlow;
  // catTurboData?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  lf: null
});

// 鼠标enter 和 leave操作显示按钮功能
let focusIndex = ref<Number>(-1);

const controlUndoButton = templateRef<HTMLElement | any>(
  "controlUndoButton",
  null
);
const controlRedoButton = templateRef<HTMLElement | any>(
  "controlRedoButton",
  null
);

const onEnter = key => {
  focusIndex.value = key;
};

let titleLists = ref([
  {
    icon: "icon-zoom-out-hs",
    text: "缩小",
    size: "18",
    disabled: false
  },
  {
    icon: "icon-enlarge-hs",
    text: "放大",
    size: "18",
    disabled: false
  },
  {
    icon: "icon-full-screen-hs",
    text: "适应",
    size: "15",
    disabled: false
  },
  {
    icon: "icon-previous-hs",
    text: "上一步",
    size: "15",
    name: "Undo",
    disabled: true
  },
  {
    icon: "icon-next-step-hs",
    text: "下一步",
    size: "17",
    name: "Redo",
    disabled: true
    // },
    // {
    //   icon: "icon-download-hs",
    //   text: "下载图片",
    //   size: "17",
    //   disabled: false
    // },
    // {
    //   icon: "icon-watch-hs",
    //   text: "查看数据",
    //   size: "17",
    //   disabled: false
  }
]);

const onControl = (item, key) => {
  /** 缩小  放大  自适应  上一步  下一步  数据*/
  ["zoom", "zoom", "resetZoom", "undo", "redo", "getSnapshot"].forEach(
    (v, i) => {
      let domControl = props.lf;
      if (key === 1) {
        domControl.zoom(true);
      }

      if (key === i) {
        // 缩小是默认行为
        domControl[v]();
      }
    }
  );
};

onMounted(() => {
  props.lf.on("history:change", ({ data: { undoAble, redoAble } }) => {
    unref(titleLists)[3].disabled = unref(controlUndoButton).disabled =
      !undoAble;
    unref(titleLists)[4].disabled = unref(controlRedoButton).disabled =
      !redoAble;
  });
});
</script>

<template>
  <div class="control-container">
    <!-- 功能按钮 -->
    <ul>
      <li
        v-for="(item, key) in titleLists"
        :key="key"
        :title="item.text"
        @mouseenter.prevent="onEnter(key)"
        @mouseleave.prevent="focusIndex = -1"
      >
        <el-tooltip
          :content="item.text"
          :visible="focusIndex === key"
          placement="right"
        >
          <button
            :ref="'control' + item.name + 'Button'"
            :disabled="item.disabled"
            :style="{
              cursor: item.disabled === false ? 'pointer' : 'not-allowed',
              color: item.disabled === false ? '' : '#00000040'
            }"
            @click="onControl(item, key)"
          >
            <span
              :class="'iconfont ' + item.icon"
              :style="{ fontSize: `${item.size}px` }"
            />
          </button>
        </el-tooltip>
      </li>
    </ul>
  </div>
</template>

<style lang="scss" scoped>
@import "./assets/iconfont/iconfont.css";

.control-container {
  background: hsla(0, 0%, 100%, 0.8);
  box-shadow: 0 2px 4px rgb(0 0 0 / 20%);
  border-radius: 6px;
  top: 10px;
  ul {
    li {
      margin: 10px;
      text-align: center;
      span:hover {
        color: var(--el-color-primary);
      }
    }
  }
}
</style>
