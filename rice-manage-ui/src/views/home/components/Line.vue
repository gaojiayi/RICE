<script setup lang="ts">
import { ECharts } from "echarts";
import echarts from "/@/plugins/echarts";
import { onBeforeMount, onMounted, nextTick, computed } from "vue";
import { useEventListener, tryOnUnmounted, useTimeoutFn } from "@vueuse/core";

let echartInstance: ECharts;

const props = defineProps({
  index: {
    type: Number,
    default: 0
  }
});

const formatDate = (time: any) => {
  // 格式化日期，获取今天的日期
  const Dates = new Date(time);
  const year: number = Dates.getFullYear();
  const month: any =
    Dates.getMonth() + 1 < 10
      ? "0" + (Dates.getMonth() + 1)
      : Dates.getMonth() + 1;
  const day: any =
    Dates.getDate() < 10 ? "0" + Dates.getDate() : Dates.getDate();
  return year + "-" + month + "-" + day;
};

const getLineData = () => {
  const week: Array<any> = [];
  for (let i = 6; i >= 0; i--) {
    week.push(formatDate(new Date().getTime() + -1000 * 3600 * 24 * i));
  }
  return {
    datax: week,
    datay: [3, 204, 1079, 1079, 1079, 2079, 4455]
  };
};

const echartData = {
  grid: {
    bottom: "20%",
    height: "68%",
    containLabel: true
  },
  tooltip: {
    trigger: "item"
  },

  xAxis: {
    type: "category",
    axisLabel: {
      interval: 0,
      show: true,
      rotate:45
    },
    data: []
  },
  yAxis: {
    type: "value"
  },
  series: [
    {
      data: [],
      type: "line",
      areaStyle: {}
    }
  ]
};

function initechartInstance() {
  const echartDom = document.querySelector(".line" + props.index);
  if (!echartDom) return;
  // @ts-ignore
  echartInstance = echarts.init(echartDom);
  echartInstance.clear(); //清除旧画布 重新渲染
  const lineData = getLineData();
  echartData.xAxis.data = lineData["datax"];
  echartData.series[0].data = lineData["datay"];
  echartInstance.setOption(echartData);
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
  <div :class="'line' + props.index" style="width: 100%; height: 35vh" />
</template>
