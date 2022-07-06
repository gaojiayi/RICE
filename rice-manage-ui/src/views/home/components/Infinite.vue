<script setup lang="ts">
import { ref, reactive } from "vue";
import { templateRef } from "@vueuse/core";
import SeamlessScroll from "/@/components/ReSeamlessScroll";

// 定义scroll是一个标签引用
const scroll = templateRef<ElRef | null>("scroll", null);
let listData = ref([
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "成功"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  },
  {
    triger_time: "2021-09-01",
    task_name: "vue-pure-admin",
    app_name: "1000",
    processor: "129.23.56.112",
    scheduler: "134.23,1.12",
    result: "失败"
  }
]);
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
      :data="listData"
      :class-option="classOption"
      class="warp"
    >
      <ul class="item">
        <li v-for="(item, index) in listData" :key="index">
          <span v-text="item.triger_time" />
          <span v-text="item.task_name" />
          <span v-text="item.app_name" />
          <span v-text="item.processor" />
          <span v-text="item.scheduler" />
          <span v-text="item.result" />
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
