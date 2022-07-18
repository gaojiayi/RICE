<script setup lang="ts">
import { ref, watch, unref, onMounted, nextTick } from "vue";
import LogicFlow from "@logicflow/core";
// 该工作流采用 LogicFlow  http://logic-flow.org/
import "@logicflow/core/dist/style/index.css";
import "@logicflow/extension/lib/style/index.css";
import { toLogicflowData,toTurboData } from "/@/components/ReFlowChart/src/adpterForTurbo";
import { Snapshot, BpmnElement, Menu } from "@logicflow/extension";

const props = defineProps({
  wfvisible: {
    type: Boolean,
    default: false
  },
  wfdata: {
    type: Object,
    default: {}
  }
});
let lf = ref(null);
const wfDialogVisible = ref(false);
watch(
  () => props.wfvisible,
  val => {
    wfDialogVisible.value = val;
    nextTick(() => {
      if (val) {
        initLf();
      }
    });
  }
);

let config = ref({
  isSilentMode: true,
  stopScrollGraph: true,
  stopZoomGraph: true,
  grid: {
    // 另外还支持dot  圆点画布
    size: 10, //小方格的大小
    type: "mesh" // 画布背景为小方格
  },
  background: {
    color: "#f7f9ff"
  },
  // keyboard: {
  //   enabled: true
  // }
});
const initLf = () => {
  // 画布配置
  LogicFlow.use(Snapshot);
  // 使用bpmn插件，引入bpmn元素，这些元素可以在turbo中转换后使用
  LogicFlow.use(BpmnElement);
  // 启动右键菜单
  LogicFlow.use(Menu);

  const domLf = new LogicFlow({
    ...unref(config),
    container: document.querySelector("#LF-Turbo") as HTMLElement
  });
  lf.value = domLf;
  // 设置边类型bpmn:sequenceFlow为默认类型   所以节点类型会固定 比如 bpmn:exclusiveGateway
  unref(lf).setDefaultEdgeType("bpmn:sequenceFlow");

  // 渲染
  onRender();
};

function onRender() {
  // Turbo数据转换为LogicFlow内部识别的数据结构
  const lFData = toLogicflowData(toTurboData(props.wfdata));
  lf.value.render(lFData);
}

// 显示控制
const emit = defineEmits(["update:show-w-f-dialog"]);

watch(
  () => wfDialogVisible.value,
  val => {
    emit("update:show-w-f-dialog", val);
  }
);
</script>

<template>
  <div>
    <el-dialog
      v-model="wfDialogVisible"
      title="流程详情"
    >
      <div id="LF-Turbo" style="width: 100%; height: 300px"></div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped></style>
