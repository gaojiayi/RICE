<script setup lang="ts">
import { ref, watch } from "vue";

const props = defineProps({
  taskInstanceId: {
    type: String,
    default: ""
  },
  logDialogVisible: {
    type: Boolean,
    default: false
  }
});

console.log(props);

const dialogVisible = ref(props.logDialogVisible);

const logData = ref([
  "18:24:04 [vite] hmr update /src/views/taskmanager/instance/components/TaskInstancedDataTable.vue (x11)",
  "18:24:17 [vite] hmr update /src/views/taskmanager/instance/components/TaskInstancedDataTable.vue (x13)",
  "18:24:41 [vite] hmr update /src/views/taskmanager/instance/components/TaskInstancedDataTable.vue (x17)",
  "18:24:51 [vite] Internal server error: unplugin-vue-define-options SyntaxError: [@vue/compiler-sfc] Missing semicolon. (10:5)",
  "8  |  });",
  "9  |  useTaskManagerHook().",
  "10 |  const props = defineProps({",
  "11 |    parentTaskInstanceId: { type: String, default: \"\" }\,",
  "12 |    childTaskTableVisible: {",
  "  Plugin: unplugin-vue-define-options",
  "File: /Users/gaojiayi/RICE/rice-manage-ui/src/views/taskmanager/instance/components/TaskInstancedDataTable.vue"]);


watch(
  () => props.logDialogVisible,
  val => {
    dialogVisible.value = val;
  }
);

const handleClose = (done: () => void) => {
  dialogVisible.value = !dialogVisible.value;
  done();
};
const emit = defineEmits(["update:log-dialog-visible"]);


watch(
  () => dialogVisible.value,
  val => {
    emit("update:log-dialog-visible", val);
  }
);




</script>

<template>
  <el-dialog
    v-model="dialogVisible"
    title="日志"
    width="50%"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <div class="log-container">
      <template v-for="(line, indiex) in logData" :key="indiex">
        <p>{{ line }}</p>
      </template>
    </div>
  </el-dialog>
</template>

<style lang="scss" scoped>
.log-container{
  height: 600px;
  // border-radius: 20%;
  z-index: 1;
  background-color: #000;
  color: #fff;
  padding: 15px;
  overflow: scroll;


}
</style>
