<script lang="ts" setup>
import { ref ,onMounted} from "vue";
import { useRenderIcon } from "/@/components/ReIcon/src/hooks";
import { ElMessage, ElMessageBox } from "element-plus";
import CreateAppDialog from "./components/CreateAppDialog.vue";
import { useRouter, RouteParamsRaw } from "vue-router";
import { appStore } from "/@/store";

const router = useRouter();

const skipToTaskCreate = (val: RouteParamsRaw) => {
  router.push({ name: "taskcreate", params: val });
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

const appInfoList = async () => {
  try {
    const { data } = await appStore.applicationStore.GET_APP_INFOS();
    console.log(data)
    //productList.value = data.list;
    pageTotal.value = data.list.length;
  } catch (e) {
    console.log(e);
  }
};

onMounted(() => {
  appInfoList();
});

const handleDeleteItem = product => {
  ElMessageBox.confirm(
    product
      ? `确认删除后${product.name}的所有产品信息将被清空, 且无法恢复`
      : "",
    "提示",
    {
      type: "warning"
    }
  )
    .then(() => {
      ElMessage({
        type: "success",
        message: "删除成功"
      });
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
          v-for="(o, index) in 6"
          :key="o"
          :span="4"
          style="margin: 20px"
          class="card-app-info"
        >
          <el-card :body-style="{ padding: '5px', height: '250px' }">
            <div class="app-header">
              <img
                src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png"
                class="image"
              />
              <div class="app-title">
                <iconify-icon-offline
                  icon="close-circle-fill"
                  @click="handleDeleteItem"
                  class="icon-remove-app"
                />
                <div class="app-title-name">订单系统</div>
                <div class="app-title-id">ID:34455</div>
                <el-button
                  @click="skipToTaskCreate({ appid: 111, appName: '订单系统' })"
                >
                  新增任务
                </el-button>
              </div>
            </div>

            <div style="">
              <div class="bottom-app-info">
                <span class=""
                  >订单系统收集了来自h5,小程序,客户端的订单请求,作为支付的重要重要凭证,订单系统努力打造订单中台,为公司提供统一的订单解决方案.</span
                >
              </div>
            </div>
          </el-card>
        </el-col>
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="appStore.applicationStore.pageSize"
          :current-page="appStore.applicationStore.pageIndex"
          :total="pageTotal"
          class="app-page"
        />
      </el-row>
    </div>

    <CreateAppDialog v-model:visible="formDialogVisible" :data="formData" />
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
