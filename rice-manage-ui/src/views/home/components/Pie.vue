<script setup lang="ts">
import { ECharts } from "echarts";
import echarts from "/@/plugins/echarts";
import { onBeforeMount, onMounted, nextTick } from "vue";
import { useEventListener, tryOnUnmounted, useTimeoutFn } from "@vueuse/core";

let echartInstance: ECharts;

const props = defineProps({
  index: {
    type: Number,
    default: 0
  }
});

function initechartInstance() {
  const echartDom = document.querySelector(".pie" + props.index);
  if (!echartDom) return;
  // @ts-ignore
  echartInstance = echarts.init(echartDom);
  echartInstance.clear(); //清除旧画布 重新渲染
  echartInstance.setOption({
    tooltip: {
      trigger: "item"
    },
    legend: {
      orient: "vertical",
      right: true
    },
    series: [
      {
        name: "任务调度统计",
        type: "pie",
        radius: ["40%", "70%"],
                center: ["40%", "50%"],

        label: {
          show: true,
          // position: "center",
          formatter: '{b}: {c}'
        },
        emphasis: {
          label: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)"
          }
        },
        labelLine: {
          show: true
        },
        data: [
          { value: 1079, name: "成功" },
          { value: 79, name: "异常" },
          { value: 204, name: "失败" },
          { value: 3, name: "超时" }
        ],
        // emphasis: {
        //   itemStyle: {
        //     shadowBlur: 10,
        //     shadowOffsetX: 0,
        //     shadowColor: "rgba(0, 0, 0, 0.5)"
        //   }
        // }
      }
    ]
  });
}

onBeforeMount(() => {
  nextTick(() => {
    initechartInstance();
  });
});

onMounted(() => {
  // nextTick  在下一次DOM更新之后立即执行
  nextTick(() => {
    // 注册事件  当前pie组件发送resize事件之后  执行下面的操作
    useEventListener("resize", () => {
      if (!echartInstance) return;
      useTimeoutFn(() => {
        echartInstance.resize();
      }, 180);
    });
  });
});

tryOnUnmounted(() => {
  if (!echartInstance) return;
  echartInstance.dispose();
  echartInstance = null;
});
</script>

<template>
  <div :class="'pie' + props.index" style="width: 100%; height: 35vh" />
</template>

<style scoped></style>
