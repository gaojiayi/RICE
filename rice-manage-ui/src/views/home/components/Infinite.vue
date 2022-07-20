<script setup lang="ts">
import { ref, reactive } from "vue";
import { templateRef } from "@vueuse/core";
import SeamlessScroll from "/@/components/ReSeamlessScroll";
import { TaskInfoIntimeType } from "/@/store/home/type";

// 定义scroll是一个标签引用
const scroll = templateRef<ElRef | null>("scroll", null);
const props = defineProps({
  listData: {
    type: Array as () => Array<TaskInfoIntimeType>,
    default: () => []
  }
});

let classOption = reactive({
  direction: "top"
});
</script>

<template>
  <div class="infinite">
    <ul class="top">
      <li>触发时间</li>
      <li>任务名称</li>
      <li>所属应用</li>
      <li>执行器</li>
      <li>调度器</li>
      <li>执行结果</li>
    </ul>
    <SeamlessScroll
      ref="scroll"
      :data="props.listData"
      :class-option="classOption"
      class="warp"
    >
      <ul class="item">
        <li v-for="(item, index) in props.listData" :key="index" style="font-size: 10px;" >
          <span v-text="item.trigger_time" />
          <span v-text="item.task_name" />
          <span v-text="item.app_name" />
          <span v-text="item.processor_address" />
          <span v-text="item.scheduler_address" />
          <span v-if="item.status == 0" v-text="'待执行'" style="color: green" />
          <span v-if="item.status == 1" v-text="'正在执行'" style="color: green" />
          <span v-if="item.status == 2" v-text="'完成'" style="color: green" />
          <span v-if="item.status == 3" v-text="'失败'" style="color: red" />
          <span v-if="item.status == 4" v-text="'超时'" style="color: red" />
        </li>
      </ul>
    </SeamlessScroll>
  </div>
</template>

<style lang="scss" scoped>
.infinite {
  .top {
    width: 95%;
    height: 40px;
    line-height: 40px;
    display: flex;
    margin: 0 auto;
    font-size: 14px;
    color: #909399;
    font-weight: 400;
    background: #fafafa;

    li {
      width: 34%;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .warp {
    width: 95%;
    height: 215px;
    margin: 0 auto;
    overflow: hidden;

    li {
      height: 30px;
      line-height: 30px;
      display: flex;
      font-size: 15px;
    }

    span {
      width: 34%;
      text-align: center;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
