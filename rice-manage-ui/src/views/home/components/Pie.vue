<script setup lang="ts">
import { ECharts } from "echarts";
import echarts from "/@/plugins/echarts";
import { onBeforeMount, onMounted, nextTick } from "vue";
import { useEventListener, tryOnUnmounted, useTimeoutFn } from "@vueuse/core";
// import { TaskSuccessRateType} from "/@/store/home/type"

interface PieDataType {
  timeout: number;
  failed: number;
  success: number;
  execption: number;
}
let echartInstance: ECharts;

const props = defineProps({
  index: {
    type: Number,
    default: 0
  },
  pieData: {
    type: Object as PropType<PieDataType>,
    default: {}
  }
});

const pieData2RenderData = () => {
  let data = [];
  data.push({ name: "成功", value: props.pieData.success });
  data.push({ name: "失败", value: props.pieData.failed });
  data.push({ name: "异常", value: props.pieData.execption });
  data.push({ name: "超时", value: props.pieData.timeout });
  return data
};

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
          formatter: "{b}: {c}"
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
        data: pieData2RenderData()

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
