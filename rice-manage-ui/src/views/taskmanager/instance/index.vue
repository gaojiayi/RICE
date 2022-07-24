<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { fetchTaskInfo } from "/@/api/task";
import { LogDialog, TaskInstanceDataTable } from "./components";
import { useTaskManagerHook} from "/@/store"
defineOptions({
  name: "taskinstanceinfo"
});

const taskInstanceStore = useTaskManagerHook().taskInstance;
const query = reactive({
  address: "",
  name: "",
  pageIndex: 1,
  pageSize: 10
});

const loading = ref(false);

const logDialogVisible = ref(false);
const logDialogTaskInstanceId = ref("");

const childTaskTableVisible = ref(false);
const parentTaskInastanceId = ref("");

const taskInfoData = ref([]);
const pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  taskInstanceStore.FETCH_INSTANCE_BY_CODE().then(res => {
    taskInfoData.value = res.list;
    pageTotal.value = res.pageTotal || 0;
  });
};
getData();

// 查询操作
const searchTaskInfo = () => {
  taskInstanceStore.queryParam.pageIndex  = 1;
  getData();
};

// 分页导航
const handlePageChange = val => {
  taskInstanceStore.queryParam.pageIndex = val;
  getData();
};

</script>

<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <!-- <span >任务编码:</span -->
        <!-- > -->
        <el-input
          v-model="taskInstanceStore.queryParam.taskCode"
          placeholder="任务编码"
          class="handle-input mr3"
        ></el-input>
        <el-button type="primary" @click="searchTaskInfo"
          ><iconify-icon-offline icon="search" />搜索</el-button
        >
      </div>
      <!-- <el-empty v-if="pageTotal === 0 && !loading" description="暂无搜索结果" /> -->
      <el-table
        :data="taskInfoData"
        v-loading="loading"
        stripe
        class="table"
        header-cell-class-name="table-header"
      >
        <el-table-column
          prop="id"
          label="Instance ID"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="taskCode"
          label="任务编码"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="instanceParams"
          label="运行参数"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="actualTriggerTime"
          label="触发时间"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="runningTimes"
          label="运行时间"
          align="center"
        ></el-table-column>
        <!-- <el-table-column
          prop="type"
          label="任务类型"
          align="center"
        ></el-table-column> -->
        <el-table-column
          prop="taskTrackerAddress"
          label="执行器地址"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="result"
          label="返回值"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="finishedTime"
          label="完成时间"
          align="center"
        ></el-table-column>

        <el-table-column prop="" label="子任务详情" align="center"
          ><template #default="scope">
          <!-- v-if="scope.row.parent_instance_id" -->
            <el-button  v-if="scope.row.parentInstanceId"
              type="text"
              @click="childTaskTableVisible = true"
              >子任务
            </el-button>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" align="center">
        </el-table-column>
        <el-table-column prop="status" label="实例运行状态" align="center">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.status === 0
                  ? 'success'
                  : scope.row.status === 1
                  ? 'danger'
                  : ''
              "
              >{{ scope.row.status === 0 ? "运行中" : "已停止" }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="日志" width="" align="center">
          <template #default="scope">
            <el-button   v-if="!scope.row.parentInstanceId"
              type="text"
              @click="
                () => {
                  logDialogVisible = true;
                  logDialogTaskInstanceId = scope.row.task_instance_id;
                }
              "
              ><IconifyIconOffline icon="log-consule" class="icon-log-consule"/>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :current-page="query.pageIndex"
          :page-size="query.pageSize"
          :total="pageTotal"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 子任务弹出框 -->
    <TaskInstanceDataTable
      :parent-task-instance-id="parentTaskInastanceId"
      :child-task-table-visible="childTaskTableVisible"
      @update:child-task-table-visible="
        val => {
          childTaskTableVisible = val;
        }
      "
    />

    <!-- 日志弹出框 -->
    <LogDialog
      :task-instance-id="logDialogTaskInstanceId"
      :log-dialog-visible="logDialogVisible"
      @update:log-dialog-visible="
        val => {
          logDialogVisible = val;
        }
      "
    />
  </div>
</template>

<style lang="scss" scoped>
.handle-box {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
  .handle-input {
    width: 10%;
  }
}
.icon-log-consule{
  font-size: 20px;
}
</style>
