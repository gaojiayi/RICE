<script lang="ts" setup>
import { ref, onMounted } from "vue";
import { useRenderIcon } from "/@/components/ReIcon/src/hooks";
import { ElMessage, ElMessageBox } from "element-plus";
import CreateAppDialog from "./components/CreateAppDialog.vue";
import { useRouter, RouteParamsRaw } from "vue-router";
import { appStore } from "/@/store";

const router = useRouter();

const skipToTaskCreate = (val: RouteParamsRaw) => {
  router.push({ name: "taskcreate", params: val });
};
type AppList = {
  appDesc: string;
  // "createTime": 1643049256753,
  appName: string;
  id: number;
  status: number;
};
const INITIAL_DATA = {
  name: "",
  status: "",
  description: "",
  type: "",
  mark: ""
};

defineOptions({
  name: "appDetail"
});

const formDialogVisible = ref(false);
const formData = ref({ ...INITIAL_DATA });
const pageTotal = ref(0);
const appInfos = ref<AppList[]>([]);

const appInfoList = async () => {
  try {
    const { data } = await appStore.applicationStore.GET_APP_INFOS();
    appInfos.value = data.appList;
    pageTotal.value = data.page.total;
  } catch (e) {
    console.log(e);
  }
};

onMounted(() => {
  appInfoList();
});

const handleDeleteItem = appInfo => {
  ElMessageBox.confirm(
    appInfo ? `确认将${appInfo.appName}的所有应用信息清空, 且无法恢复` : "",
    "提示",
    {
      type: "warning"
    }
  )
    .then(() => {
      appStore.applicationStore
        .DELETE_APP(appInfo.id)
        .then(resp => {
          if (resp["resp_code"] === 200) {
            ElMessage({
              type: "success",
              message: "删除成功"
            });
            appInfoList();
          } else {
            ElMessage({
              type: "error",
              message: "删除失败"
            });
          }
        })
        .catch();
    })
    .catch(() => {});
};
</script>

<template>
  <div class="main">
    <div class="w-full flex justify-between mb-4">
      <el-button @click="formDialogVisible = true" class="button-create-app">
        <iconify-icon-offline icon="add-circle-line" />新建应用
      </el-button>
      <el-input
        style="width: 300px; margin-right: 100px"
        v-model="appStore.applicationStore.appName"
        placeholder="请输入应用名称"
        clearable
        @keyup.enter.native="appInfoList"
      >
        <template #suffix>
          <el-icon class="el-input__icon">
            <IconifyIconOffline
              v-show="appStore.applicationStore.appName.length === 0"
              icon="search"
            />
          </el-icon>
        </template>
      </el-input>
    </div>

    <div>
      <el-row class="row-app-card-data">
        <el-col
          v-for="(appInfo, index) in appInfos"
          :key="index"
          :span="4"
          style="margin: 20px"
          class="card-app-info"
        >
          <el-card :body-style="{ padding: '5px', height: '250px' }">
            <div class="app-header">
              <img src="/@/assets/app_image.png" class="image" />
              <div class="app-title">
                <iconify-icon-offline
                  icon="close-circle-fill"
                  @click="handleDeleteItem(appInfo)"
                  class="icon-remove-app"
                />
                <div class="app-title-name">{{ appInfo.appName }}</div>
                <div class="app-title-id">ID:{{ appInfo.id }}</div>
                <el-button
                  @click="
                    skipToTaskCreate({
                      appid: appInfo.id,
                      appName: appInfo.appName
                    })
                  "
                >
                  新增任务
                </el-button>
              </div>
            </div>

            <div style="">
              <div class="bottom-app-info">
                <span class="">{{ appInfo.appDesc }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-pagination
          background
          layout="prev, pager, next"
          v-model:page-size="appStore.applicationStore.pageSize"
          v-model:current-page="appStore.applicationStore.pageIndex"
          :total="pageTotal"
          class="app-page"
        />
      </el-row>
    </div>

    <CreateAppDialog
      v-model:visible="formDialogVisible"
      :data="formData"
      @reload="appInfoList"
    />
  </div>
</template>

<style lang="scss" scoped>
.row-app-card-data {
  position: relative;
  .app-page {
    position: absolute;
    bottom: -40px;
    right: 40px;
  }
}

.bottom-app-info {
  margin-top: 30px;
  line-height: 22px;
  font-size: 12px;
  color: #999;
}

.image,
.app-title {
  width: 50%;
  /* display: inline; */
}
.image {
  -webkit-filter: drop-shadow(
    10px 10px 10px rgba(0, 0, 0, 0.5)
  ); /*考虑浏览器兼容性：兼容 Chrome, Safari, Opera */
  filter: drop-shadow(10px 10px 10px rgba(0, 0, 0, 0.5));
}
.app-title {
  font-size: 16px;
  .app-title-name {
    font-weight: bolder;
    line-height: 50px;
    font-size: 20px;
  }
  .app-title-id {
    color: #999;
  }
}
.app-header {
  display: flex;
  align-items: center;
  text-align: center;
}
.button-create-app {
  margin-left: 20px;
}
.card-app-info {
  position: relative;
  .icon-remove-app {
    position: absolute;
    right: -12.5px;
    // 举例相对定位元素的上边距-12.5px
    top: -12.5px;
    width: 25px;
    height: 25px;
    color: rgb(85, 78, 85);
  }
  .icon-remove-app:hover {
    width: 30px;
    height: 30px;
  }
}
</style>
