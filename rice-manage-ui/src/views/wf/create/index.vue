<script setup lang="ts">
// 该工作流采用 LogicFlow  http://logic-flow.org/
import "@logicflow/core/dist/style/index.css";
import "@logicflow/extension/lib/style/index.css";
import { onMounted, ref, unref } from "vue";

import LogicFlow from "@logicflow/core";

defineOptions({
  name: "WFTaskCreate"
});
let lf = ref(null);

let config = ref({
  grid: {
    // 另外还支持dot  圆点画布
    size: 10, //小方格的大小
    type: "mesh" // 画布背景为小方格
  },
  background: {
    color: "#f7f9ff"
  },
  keyboard: {
    enabled: true
  }
});
const initLf = () => {
  const domLf = new LogicFlow({
    ...unref(config),
    container: document.querySelector("#LF-Turbo")
  });
  lf.value = domLf;
  // 设置边类型bpmn:sequenceFlow为默认类型
  unref(lf).setDefaultEdgeType("bpmn:sequenceFlow");

  // 渲染
  lf.value.render();
};

onMounted(() => {
  initLf();
});
</script>

<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span class="font-medium"> DAG工作流任务编排 </span>
      </div>
    </template>
    <div class="logic-flow-view">
      <!-- 辅助工具栏 -->
      <!-- <Control
        class="demo-control"
        v-if="lf"
        :lf="lf"
        :catTurboData="false"
        @catData="catData"
      /> -->
      <!-- 节点面板 -->
      <!-- <NodePanel :lf="lf" :nodeList="nodeList" /> -->
      <!-- 画布 -->
      <div id="LF-Turbo" />
      <!-- 数据查看面板 -->
      <!-- <el-dialog
        customClass="flow-dialog"
        title="数据"
        v-model="dataVisible"
        width="50%"
      >
        <el-scrollbar>
          <DataDialog :graphData="graphData" />
        </el-scrollbar>
      </el-dialog> -->
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
#LF-Turbo {
  width: 100%;
  height: 70vh;
}
</style>
