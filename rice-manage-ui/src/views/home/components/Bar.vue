<script setup lang="ts">
import { ECharts } from "echarts";
import echarts from "/@/plugins/echarts";
import { onBeforeMount, onMounted, nextTick, PropType } from "vue";
import { useEventListener, tryOnUnmounted, useTimeoutFn } from "@vueuse/core";
import { propTypes } from "/@/utils/propTypes";

let echartInstance: ECharts;

interface ColumnProps {
  app_name: string;
  processor_num: number;
}
const props = defineProps({
  index: {
    type: Number,
    default: 0
  },
  barData: {
    type: Array as PropType<ColumnProps[]>,
    default: []
  }
});

function initechartInstance() {
  const echartDom = document.querySelector(".bar" + props.index);
  if (!echartDom) return;
  // @ts-ignore
  echartInstance = echarts.init(echartDom);
  echartInstance.clear(); //清除旧画布 重新渲染
  let barDataX = [];
  let barDataY = [];

  props.barData.forEach(item => {
    barDataX.push(item.app_name);
    barDataY.push(item.processor_num);
  });

  echartInstance.setOption({
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow"
      }
    },
    grid: {
      bottom: "20%",
      height: "68%",
      containLabel: true
    },
    xAxis: [
      {
        type: "category",
        axisTick: {
          alignWithLabel: true
        },
        axisLabel: {
          interval: 0
          // width: "70",
          // overflow: "truncate"
        },
        data: barDataX
      }
    ],
    yAxis: [
      {
        type: "value"
      }
    ],
    series: [
      {
        name: "执行服务器数量",
        type: "bar",
        data: barDataY,
        itemStyle: {
          color: "#31b485" // 定义柱子的颜色
        }
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
  nextTick(() => {
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
  <div :class="'bar' + props.index" style="width: 100%; height: 35vh" />
</template>
