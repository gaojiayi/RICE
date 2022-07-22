<script setup lang="ts">
import { ref, reactive, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRenderIcon } from "/@/components/ReIcon/src/hooks";
import { useTaskManagerHook } from "/@/store";
defineOptions({
  name: "taskmanagerlist"
});
const taskListStore = useTaskManagerHook().taskList;

const scheduleTypeOptionList = [
  {
    value: "CRON",
    label: "CRON"
  },
  {
    value: "FIXED_DELAY",
    label: "固定延迟"
  },
  {
    value: "FIXED_FREQUENCY",
    label: "固定频率"
  }
];

// 获取表格数据
const getData = () => {
  taskListStore.FETCH_ALL_TASK();
};
getData();

// 查询操作
const searchTaskInfo = () => {
  getData();
};

// 分页导航
const handlePageChange = val => {
  taskListStore.query.pageIndex = val;
  getData();
};

// 停止操作
const handleStop = index => {
  // 二次确认删除
  ElMessageBox.confirm("确定要停止任务吗？", "提示", {
    type: "warning"
  })
    .then(() => {
      taskListStore.data[index].status = 1;
      ElMessage.success("任务成功");
    })
    .catch(() => {});
};

// 启动操作
const handleStart = index => {
  // 二次确认删除
  ElMessageBox.confirm("确定要启动任务吗？", "提示", {
    type: "warning"
  })
    .then(() => {
      taskListStore.data[index].status = 0;
      ElMessage.success("任务启动成功");
    })
    .catch(() => {});
};

// 运行操作
const handleRun = index => {
  // 二次确认删除
  ElMessageBox.confirm("立即运行任务？", "提示", {
    type: "warning"
  })
    .then(() => {
      taskListStore.data[index].status = 0;
      ElMessage.success("任务成功触发");
    })
    .catch(() => {});
};


// 表格编辑时弹窗和保存
const editVisible = ref(false);
let form = reactive({
  task_code: "",
  task_name: "",
  scheduler_policy: "",
  task_retry_count: 0,
  parameters: "",
  schedule_type: "",
  time_expression: ""
});
let idx = -1;
const handleEdit = (index, row) => {
  idx = index;
  Object.keys(form).forEach(item => {
    form[item] = row[item];
  });

  editVisible.value = true;
};
const saveEdit = () => {
  editVisible.value = false;
  Object.keys(form).forEach(item => {
    taskListStore.data[idx][item] = form[item];
  });
  ElMessage.success(`修改任务成功`);
};
</script>

<template>
  <div>
    <div class="container">
      <div class="flex justify-end handle-box">
        <!-- <span >任务编码:</span -->
        <!-- > -->
        <el-input
          v-model="taskListStore.query.taskCode"
          placeholder="任务编码"
          class="handle-input mr3"
        ></el-input>
        <!-- <span >应用ID:</span> -->
        <el-input
          v-model="taskListStore.query.appName"
          placeholder="应用名称"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" @click="searchTaskInfo"
          ><iconify-icon-offline icon="search" />搜索</el-button
        >
      </div>
      <el-table
        :data="taskListStore.data"
        border
        class="table"
        ref="multipleTable"
        header-cell-class-name="table-header"
      >
        <el-table-column prop="id" label="ID" align="center"></el-table-column>
        <el-table-column
          prop="app_name"
          label="应用名称"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_code"
          label="任务编码"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_name"
          label="任务名称"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_type"
          label="任务类型"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="parameters"
          label="任务参数"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="scheduler_server"
          label="托管调度器"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="scheduler_policy"
          label="调度策略"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="task_retry_count"
          label="允许重试次数"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="next_trigger_time"
          label="下次触发时间"
          align="center"
        ></el-table-column>
        <el-table-column prop="create_time" label="创建时间" align="center">
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center">
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
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <div class="operation-icon">
              <el-tooltip class="" effect="dark" content="编辑">
                <component
                  :is="useRenderIcon('edit-fill')"
                  @click="handleEdit(scope.$index, scope.row)"
                  class="edit-operation-icon"
                />
              </el-tooltip>
              <el-tooltip
                class=""
                effect="dark"
                content="停止"
                v-if="scope.row.status === 0"
              >
                <component
                  :is="useRenderIcon('stop-fill')"
                  @click="handleStop(scope.$index)"
                  class="stop-operation-icon"
                />
              </el-tooltip>
              <el-tooltip
                class=""
                effect="dark"
                content="启动"
                v-if="scope.row.status === 1"
              >
                <component
                  :is="useRenderIcon('start-fill')"
                  @click="handleStart(scope.$index)"
                  class="start-operation-icon"
                />
              </el-tooltip>
              <el-tooltip
                class=""
                effect="dark"
                content="立即运行"
                v-if="scope.row.status === 0"
              >
                <component
                  :is="useRenderIcon('restart-fill')"
                  @click="handleRun(scope.$index)"
                  class="restart-operation-icon"
                />
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :current-page="taskListStore.query.pageIndex"
          :page-size="taskListStore.query.pageSize"
          :total="taskListStore.pageTotal"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>

    <!-- 编辑弹出框 -->
    <el-dialog
      :title="'编辑任务:' + form.task_code"
      v-model="editVisible"
      width="30%"
    >
      <el-form label-width="100px">
        <el-form-item label="任务名称">
          <el-input v-model="form.task_name"></el-input>
        </el-form-item>
        <el-form-item label="任务参数">
          <el-input v-model="form.parameters"></el-input>
        </el-form-item>
        <el-form-item label="调度类型">
          <el-select
            v-model="form.schedule_type"
            filterable
            :reserve-keyword="false"
            placeholder="选择任务调度类型"
          >
            <el-option
              v-for="item in scheduleTypeOptionList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间表达式">
          <el-input v-model="form.time_expression"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveEdit">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.handle-box {
  margin-bottom: 20px;
  .handle-input {
    width: 10%;
  }
}
.operation-icon {
  font-size: 20px;
  display: flex;
  justify-content: space-between;
  .edit-operation-icon {
    color: gray;
  }
  .stop-operation-icon {
    color: red;
  }
  .start-operation-icon {
    color: rgb(15, 197, 15);
  }
  .restart-operation-icon {
    color: rgb(15, 197, 15);
  }
}

// el-table-column {
//   el-button {
//     display: block;
//   }
// }
</style>
