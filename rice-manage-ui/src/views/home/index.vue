<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { ReInfinite, RePie, ReLine, ReBar } from "./components";
import { useHomeStoreHook } from "/@/store";

const homeStore = useHomeStoreHook();

const size = ref("");
const current_time = ref("");
const change_clock = (time: string) => {
  console.log(time);
  current_time.value = time;
};

const date: Date = new Date();
let loading = ref<boolean>(true);
setTimeout(() => {
  loading.value = !loading.value;
}, 800);

const iconStyle = computed(() => {
  const marginMap = {
    large: "8px",
    default: "6px",
    small: "4px"
  };
  return {
    marginRight: marginMap[size.value] || marginMap.default
  };
});
const blockMargin = computed(() => {
  const marginMap = {
    large: "32px",
    default: "28px",
    small: "24px"
  };
  return {
    marginTop: marginMap[size.value] || marginMap.default
  };
});

onMounted(() => {
  homeStore.getStatistic();
  homeStore.getCollectors();
});
</script>

<template>
  <div>
    <!--  gutter 表示分栏间隔   -->
    <el-row :gutter="24" style="margin: 20px">
      <!-- xs 在比较小宽度（手机），每分栏占据屏幕全部宽度。 -->
      <!-- lg 在比较大的分辨率（电脑），每分栏占据屏幕宽度的1/2-->
      <el-col
        :xs="24"
        :sm="24"
        :md="12"
        :lg="12"
        :xl="12"
        class="col-base-top-info"
        style="margin-bottom: 20px"
        v-motion
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 200
          }
        }"
      >
        <el-row>
          <el-col :span="8" style="padding-right: 5px">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-1">
                <div class="grid-con-icon">
                  <IconifyIconOffline
                    icon="list-check"
                    class="icon-task-total"
                  />
                </div>

                <div class="grid-cont-right">
                  <div class="grid-num">{{ homeStore.statistic.task_num }}</div>
                  <div>任务总数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8" style="padding: 0px 2.5px">
            <el-card
              shadow="hover"
              :body-style="{ padding: '0px', 'border-radius': '10px' }"
            >
              <div class="grid-content grid-con-2">
                <div class="grid-con-icon">
                  <IconifyIconOffline
                    icon="24-hour-line"
                    class="icon-schedule-total"
                  />
                </div>

                <div class="grid-cont-right">
                  <div class="grid-num">
                    {{ homeStore.statistic.total_schedule_times }}
                  </div>
                  <div>总调度次数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8" style="padding-left: 5px">
            <el-card shadow="always" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-con-3">
                <div class="grid-con-icon">
                  <IconifyIconOffline
                    icon="computer-line"
                    class="icon-server-total"
                  />
                </div>
                <div class="grid-cont-right">
                  <div class="grid-num">
                    {{ homeStore.statistic.scheduler_num }}
                  </div>
                  <div>调度服务器数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="24">
            <el-card shadow="always" class="card-controller-info">
              <el-descriptions class="" title="" :column="1">
                <el-descriptions-item
                  label="当前控制器地址:"
                  class-name="descriptions-item-label"
                  >{{
                    homeStore.current_controller_info.current_address
                  }}</el-descriptions-item
                >
                <el-descriptions-item label="主/从:">
                  <el-tag
                    v-if="homeStore.current_controller_info.isMaster"
                    type="danger"
                    size="small"
                    >主控制器</el-tag
                  >
                  <el-tag v-else type="success" size="small">从控制器</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="启动时间:">{{
                  homeStore.current_controller_info.start_time
                }}</el-descriptions-item>

                <el-descriptions-item label="状态:">
                  <el-tag
                    v-if="homeStore.current_controller_info.status === 0"
                    size="small"
                    >运行中</el-tag
                  >
                  <el-tag v-else type="info" size="small">已停机</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="其他控制器:">
                  <el-tag
                    v-for="item in homeStore.current_controller_info
                      .other_collectors"
                    :key="item.address"
                    :type="item.isMaster ? 'danger' : 'info'"
                    class="mx-1"
                    effect="dark"
                    round
                  >
                    {{ item.address }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <el-col
        :xs="24"
        :sm="24"
        :md="12"
        :lg="12"
        :xl="12"
        style="margin-bottom: 20px"
        v-motion
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 200
          }
        }"
      >
        <el-card style="height: 360px">
          <template #header>
            <span style="font-size: 16px; font-weight: 500">
              调度实时动态
            </span>
          </template>
          <el-skeleton animated :rows="7" :loading="loading">
            <template #default>
              <ReInfinite />
            </template>
          </el-skeleton>
        </el-card>
      </el-col>

      <el-col
        :xs="24"
        :sm="24"
        :md="24"
        :lg="8"
        :xl="8"
        style="margin-bottom: 20px"
        v-motion
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 400
          }
        }"
      >
        <el-card>
          <template #header>
            <span style="font-size: 16px; font-weight: 500"> 任务成功率 </span>
          </template>
          <el-skeleton animated :rows="7" :loading="loading">
            <template #default>
              <RePie />
            </template>
          </el-skeleton>
        </el-card>
      </el-col>

      <el-col
        :xs="24"
        :sm="24"
        :md="24"
        :lg="8"
        :xl="8"
        style="margin-bottom: 20px"
        v-motion
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 400
          }
        }"
      >
        <el-card>
          <template #header>
            <span style="font-size: 16px; font-weight: 500">
              应用执行器数
            </span>
          </template>
          <el-skeleton animated :rows="7" :loading="loading">
            <template #default>
              <ReBar />
            </template>
          </el-skeleton>
        </el-card>
      </el-col>

      <el-col
        :xs="24"
        :sm="24"
        :md="12"
        :lg="8"
        :xl="8"
        style="margin-bottom: 20px"
        v-motion
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 400
          }
        }"
      >
        <el-card>
          <template #header>
            <span style="font-size: 16px; font-weight: 500">
              单日调度统计
            </span>
          </template>
          <el-skeleton animated :rows="7" :loading="loading">
            <template #default>
              <ReLine />
            </template>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.cell-item {
  display: flex;
  align-items: center;
}

.card-controller-info {
  margin-top: 38px;
  height: 220px;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 100px;
  flex: 1;
  justify-content: space-between;
}
.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: rgb(92, 89, 89);
}
.grid-num {
  font-size: 30px;
  font-weight: bold;
}
.grid-con-icon {
  width: 100px;
  height: 100px;
  font-size: 50px;
  text-align: center;
  line-height: 100px;
  color: #fff;
}
$grid1-color: rgb(45, 140, 240);
$grid2-color: rgb(45, 240, 64);
$grid3-color: rgb(126, 19, 227);
.grid-con-1 {
  .grid-con-icon {
    background: $grid1-color;
    .icon-task-total {
      margin: 25px;
    }
  }
  .grid-num {
    color: $grid1-color;
  }
}
.grid-con-2 {
  .grid-con-icon {
    background: $grid2-color;
    .icon-schedule-total {
      margin: 25px;
    }
  }
  .grid-num {
    color: $grid2-color;
  }
}

.grid-con-3 {
  .grid-con-icon {
    background: $grid3-color;
    .icon-server-total {
      margin: 25px;
    }
  }
  .grid-num {
    color: $grid3-color;
  }
}
</style>
