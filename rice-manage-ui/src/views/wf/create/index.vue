<script setup lang="ts">
// 该工作流采用 LogicFlow  http://logic-flow.org/
import "@logicflow/core/dist/style/index.css";
import "@logicflow/extension/lib/style/index.css";
import { onMounted, ref, unref, reactive } from "vue";
import { NodePanel, ControlPanel, DataDialog } from "/@/components/ReFlowChart";
import LogicFlow from "@logicflow/core";
import { Snapshot, BpmnElement, Menu } from "@logicflow/extension";

defineOptions({
  name: "WFTaskCreate"
});
let lf = ref(null);
const wf = reactive({
  name: "",
  cron: ""
});
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
  // 画布配置
  LogicFlow.use(Snapshot);
  // 使用bpmn插件，引入bpmn元素，这些元素可以在turbo中转换后使用
  LogicFlow.use(BpmnElement);
  // 启动右键菜单
  LogicFlow.use(Menu);

  const domLf = new LogicFlow({
    ...unref(config),
    container: document.querySelector("#LF-Turbo")
  });
  lf.value = domLf;
  // 设置边类型bpmn:sequenceFlow为默认类型   所以节点类型会固定 比如 bpmn:exclusiveGateway
  unref(lf).setDefaultEdgeType("bpmn:sequenceFlow");

  // 渲染
  lf.value.render();
};

onMounted(() => {
  initLf();
});

const clearLogicFlow = () => {
  initLf();
};
let graphData = ref(null);
let dataVisible = ref<boolean>(false);
function catData() {
  graphData.value = unref(lf).getGraphData();
  console.log(graphData.value);
  dataVisible.value = true;
}

let saveVisible = ref<boolean>(false);
</script>

<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div class="dag-create-title">DAG工作流任务编排</div>
        <div class="dag-create-button">
          <el-button type="primary" @click="saveVisible = true">保存</el-button>
          <el-button @click="catData">查看</el-button>
          <el-button @click="clearLogicFlow">清空</el-button>
        </div>
      </div>
    </template>
    <div class="logic-flow-view">
      <!-- 辅助工具栏 -->
      <ControlPanel class="dag-control" v-if="lf" :lf="lf" />
      <!-- 节点面板 -->
      <NodePanel :lf="lf" />
      <!-- 画布 -->
      <div id="LF-Turbo" />
      <!-- 数据查看面板 -->
      <el-dialog
        customClass="flow-dialog"
        title="数据"
        v-model="dataVisible"
        width="50%"
      >
        <el-scrollbar>
          <DataDialog :graphData="graphData" />
        </el-scrollbar>
      </el-dialog>
      <!-- 保存面板 -->
      <el-dialog
        customClass="save-dialog"
        title="保存"
        v-model="saveVisible"
        width="50%"
      >
      <el-divider />
        <el-form label-width="120px" :model="wf" style="max-width: 460px">
          <el-form-item label="工作流任务名称:">
            <el-input v-model="wf.name" />
          </el-form-item>

          <el-form-item label="cron执行表达式:">
            <el-input v-model="wf.cron" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="saveVisible = false">Cancel</el-button>
            <el-button type="primary" @click="saveVisible = false"
              >Confirm</el-button
            >
          </span>
        </template>
      </el-dialog>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
#LF-Turbo {
  width: 100%;
  height: 70vh;
}

.logic-flow-view {
  margin: 10px;
  position: relative;
  .dag-control {
    position: absolute;
    right: 10px;
    width: 50px;
    z-index: 1;
  }
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .dag-create-button {
    button {
      margin-left: 30px;
    }
  }
}
</style>
