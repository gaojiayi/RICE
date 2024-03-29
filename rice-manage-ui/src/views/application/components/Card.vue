<script setup lang="ts">
import { computed, PropType } from "vue";
import serviceIcon from "/@/assets/svg/service.svg?component";

export interface taskType {
  task_code: string;
  task_status: number;
}
export interface CardPrcessorType {
  app_name: string;
  server_status: number;
  address: string;
  port: number;
  task: taskType[];
}

defineOptions({
  name: "ReCard"
});

const props = defineProps({
  processor: {
    type: Object as PropType<CardPrcessorType>
  }
});
const emit = defineEmits(["delete-item"]);

const handleClickShield = (processor: CardPrcessorType) => {
  emit("delete-item", processor);
};

const cardClass = computed(() => [
  "list-card-item",
  { "list-card-item__disabled": props.processor.server_status == 1 }
]);

const cardLogoClass = computed(() => [
  "list-card-item_detail--logo",
  {
    "list-card-item_detail--logo__disabled": props.processor.server_status == 1
  }
]);
</script>

<template>
  <div :class="cardClass">
    <div class="list-card-item_detail">
      <el-row justify="space-between">
        <div :class="cardLogoClass">
          <serviceIcon />
        </div>
        <div class="list-card-item_detail--operation">
          <el-tag
            :color="processor.server_status == 0 ? '#00a870' : '#eee'"
            effect="dark"
            class="mx-1 list-card-item_detail--operation--tag"
          >
            {{ processor.server_status == 0 ? "运行中" : "已下线" }}
          </el-tag>
          <el-dropdown
            trigger="click"
            :disabled="processor.server_status != 0"
            max-height="2"
          >
            <IconifyIconOffline icon="more-vertical" class="icon-more" />
            <template #dropdown>
              <el-dropdown-menu :disabled="processor.server_status == 1">
                <el-dropdown-item @click="handleClickShield(processor)">
                  隔离
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <div class="server-address-text">{{ processor.address }}</div>
          <!-- <div>port: {{ processor.port }}</div> -->
        </div>
      </el-row>
      <h2>{{ processor.app_name }}</h2>
      <div class="list-card-item_detail--list">
        <template v-for="t in processor.task" :key="t.task_code">
          <p class="list-card-item_detail--task">
            <iconify-icon-offline
              icon="checkbox-blank-circle-fill"
              :class="
                t.task_status === 0
                  ? 'icon-task-status'
                  : 'icon-task-status' + ' icon-task-offline'
              "
            />
            {{ t.task_code }}
          </p>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$text-color-disabled: rgba(0, 0, 0, 0.26);
@keyframes tast-status-animation {
  from {
    color: $text-color-disabled;
  }
  to {
    color: rgb(0, 168, 112);
  }
}
.list-card-item {
  display: flex;
  height: 250px;
  flex-direction: column;
  margin-bottom: 12px;
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
  color: rgba(0, 0, 0, 0.6);
  // width: 300px;
  // height: 300px;

  &_detail {
    flex: 1;
    background: #fff;
    padding: 24px 32px;
    min-height: 140px;

    &--logo {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      justify-content: center;
      align-items: center;
      background: #e0ebff;
      font-size: 32px;
      color: #0052d9;

      &__disabled {
        color: #a1c4ff;
      }
    }
    .list-card-item_detail--list {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      justify-content: center;
      overflow: hidden;
      text-overflow: ellipsis;
      .list-card-item_detail--task {
        font-size: 16px;
        line-height: 30px;
        .icon-task-status {
          display: inline-block;
          font-size: 12px;
          color: rgb(0, 168, 112);
          animation: tast-status-animation 0.9s infinite;
        }
        .icon-task-offline {
          color: $text-color-disabled;
          animation-play-state: paused;
        }
      }
    }

    h2 {
      margin-top: 20px;
    }
    &--operation {
      // display: flex;
      // height: 100%;
      position: relative;
      &--tag {
        position: absolute;
        border: 0;
        // top: 0px;
        // margin-right: 10px;
      }
      .el-dropdown {
        position: absolute;
        top: 0px;
        right: 0px;
      }
      .server-address-text {
        margin-top: 30px;
        text-align: center;
        // color: #2d69b7;
      }
    }

    .icon-more {
      font-size: 24px;
      color: rgba(36, 36, 36, 1);
    }

    &--name {
      margin: 24px 0 8px 0;
      font-size: 16px;
      font-weight: 400;
      color: rgba(0, 0, 0, 0.9);
    }

    // &--desc {
    //   font-size: 12px;
    //   line-height: 20px;
    //   overflow: hidden;
    //   text-overflow: ellipsis;
    //   display: -webkit-box;
    //   -webkit-line-clamp: 2;
    //   -webkit-box-orient: vertical;
    //   margin-bottom: 24px;
    //   height: 40px;
    // }
  }

  &__disabled {
    color: $text-color-disabled;

    .icon-more {
      color: $text-color-disabled;
    }

    .list-card-item_detail--name {
      color: $text-color-disabled;
    }

    .list-card-item_detail--operation--tag {
      color: #bababa;
    }
  }
}
</style>
