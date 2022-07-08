<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { fetchTaskInfo } from "/@/api/task";

defineOptions({
  name: "taskmanager"
});

const query = reactive({
  address: "",
  name: "",
  pageIndex: 1,
  pageSize: 10
});

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

// 查询操作
const searchTaskInfo = () => {
  query.pageIndex = 1;
  getData();
};

// 分页导航
const handlePageChange = val => {
  query.pageIndex = val;
  getData();
};

// 删除操作
const handleDelete = index => {
  // 二次确认删除
  ElMessageBox.confirm("确定要删除吗？", "提示", {
    type: "warning"
  })
    .then(() => {
      ElMessage.success("删除成功");
      taskInfoData.value.splice(index, 1);
    })
    .catch(() => {});
};

// 表格编辑时弹窗和保存
const editVisible = ref(false);
let form = reactive({
  name: "",
  address: ""
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
  ElMessage.success(`修改第 ${idx + 1} 行成功`);
  Object.keys(form).forEach(item => {
    taskInfoData.value[idx][item] = form[item];
  });
};
</script>

<template>
  <div>
    <div class="container">
      <div class="handle-box">
       <!-- <span >任务编码:</span -->
      <!-- > -->
        <el-input
          v-model="query.name"
          placeholder="任务编码"
          class="handle-input mr3"
        ></el-input>
        <!-- <span >应用ID:</span> -->
        <el-input
          v-model="query.name"
          placeholder="应用ID"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary"  @click="searchTaskInfo"
          ><iconify-icon-offline icon="search"/>搜索</el-button
        >
      </div>
      <el-table
        :data="taskInfoData"
        border
        class="table"
        ref="multipleTable"
        header-cell-class-name="table-header"
      >
        <el-table-column
          prop="id"
          label="ID"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="app_name"
          label="应用名称"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="task_code"
          label="任务编码"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="task_name"
          label="任务名称"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="task_type"
          label="任务类型"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="parameters"
          label="任务参数"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="scheduler_server"
          label="托管调度器"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="scheduler_policy"
          label="调度策略"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="task_retry_count"
          label="允许重试次数"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="next_trigger_time"
          label="下次触发时间"

          align="left"
        ></el-table-column>
        <el-table-column
          prop="create_time"
          label="创建时间"

          align="left"
        >
        </el-table-column>
        <el-table-column prop="status" label="状态" align="left">
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
        <el-table-column label="操作" width="180" align="left">
          <template #default="scope">
            <el-button
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.$index, scope.row)"
              >编辑
            </el-button>
            <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="handleDelete(scope.$index)"
              v-if="scope.row.status === 0"
              >停止</el-button
            >
            <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="handleDelete(scope.$index)"
              v-if="scope.row.status === 1"
              >启动</el-button
            >
             <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="handleDelete(scope.$index)"
              v-if="scope.row.status === 0"
              >立即运行</el-button
            >
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

    <!-- 编辑弹出框 -->
    <el-dialog title="编辑" v-model="editVisible" width="30%">
      <el-form label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address"></el-input>
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
.handle-box{
  margin-bottom: 20px;
  .handle-input{
    width: 10%;
  }
}
</style>
