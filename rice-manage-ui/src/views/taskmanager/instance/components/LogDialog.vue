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

const logData = ref(["sdd","dfghjk","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d","d",]);


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
