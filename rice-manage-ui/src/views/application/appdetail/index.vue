<script lang="ts" setup>
import { ref } from "vue";
import { useRenderIcon } from "/@/components/ReIcon/src/hooks";

defineOptions({
  name: "appDetail"
});
const currentDate = ref(new Date());

const formDialogVisible = ref(false);
const searchValue = ref("");
</script>

<template>
  <div class="main">
    <div class="w-full flex justify-between mb-4">
      <el-button :icon="useRenderIcon('add')" @click="formDialogVisible = true">
        新建应用
      </el-button>
      <el-input
        style="width: 300px"
        v-model="searchValue"
        placeholder="请输入应用名称"
        clearable
      >
        <template #suffix>
          <el-icon class="el-input__icon">
            <IconifyIconOffline
              v-show="searchValue.length === 0"
              icon="search"
            />
          </el-icon>
        </template>
      </el-input>
    </div>

    <div>
      <el-row>
        <el-col
          v-for="(o, index) in 2"
          :key="o"
          :span="8"
          :offset="index > 0 ? 2 : 0"
        >
          <el-card :body-style="{ padding: '0px' }">
            <img
              src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png"
              class="image"
            />
            <div style="padding: 14px">
              <span>订单系统</span>
              <div class="bottom">
                <span class="time">{{ currentDate }}</span>
                <el-button text class="button">编辑</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<style>
.time {
  font-size: 12px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button {
  padding: 0;
  min-height: auto;
}

.image {
  width: 100%;
  display: block;
}
</style>
