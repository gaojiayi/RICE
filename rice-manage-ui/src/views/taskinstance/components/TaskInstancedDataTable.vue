<script setup lang="ts">
import { ref, reactive ,watch} from "vue";
import { fetchTaskInfo } from "/@/api/task";
import { LogDialog } from ".";

defineOptions({
  name: "childtaskinstanceinfo"
});

const props = defineProps({
  parentTaskInstanceId: { type: String, default: "" },
  childTaskTableVisible: {
    type: Boolean,
    default: false
  }
});
const query = reactive({
  address: "",
  name: "",
  pageIndex: 1,
  pageSize: 10
});

const childTaskTableVisible = ref(props.childTaskTableVisible);

const loading = ref(false);
const logDialogVisible = ref(false);
const logDialogTaskInstanceId = ref("");
const taskInfoData = ref([]);
const pageTotal = ref(0);

// 获取表格数据
const getData = () => {
  fetchTaskInfo(query).then(res => {
    taskInfoData.value = res.list;
    pageTotal.value = res.pageTotal || 50;
  });
};
getData();

// 分页导航
const handlePageChange = val => {
  query.pageIndex = val;
  getData();
};



watch(
  () => props.childTaskTableVisible,
  val => {
    childTaskTableVisible.value = val;
  }
);

const handleClose = (done: () => void) => {
  childTaskTableVisible.value = !childTaskTableVisible.value;
  done();
};
const emit = defineEmits(["update:child-task-table-visible"]);


watch(
  () => childTaskTableVisible.value,
  val => {
    emit("update:child-task-table-visible", val);
  }
);



</script>

<template>
  <el-dialog
    v-model="childTaskTableVisible"
    title="子任务"
    width="80%"
    :before-close="handleClose"
  >
    <div class="container">

      <el-empty v-if="pageTotal === 0 && !loading" description="暂无搜索结果" />
      <el-table
        :data="taskInfoData"
        v-loading="loading"
        stripe
        class="table"
        ref="multipleTable"
        header-cell-class-name="table-header"
      >
        <el-table-column
          prop="id"
          label="Instance ID"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_code"
          label="任务编码"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="instance_params"
          label="运行参数"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="actual_trigger_time"
          label="触发时间"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="running_times"
          label="运行时间"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="type"
          label="任务类型"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_tracker_address"
          label="执行器地址"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="result"
          label="返回值"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="finished_time"
          label="完成时间"
          align="center"
        ></el-table-column>

        <el-table-column prop="create_time" label="创建时间" align="center">
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
            <el-button
              type="text"
              @click="
                () => {
                  logDialogVisible = true;
                  logDialogTaskInstanceId = scope.row.task_instance_id;
                }
              "
              ><IconifyIconOffline icon="log-consule" />
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
 </el-dialog>
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
</style>
