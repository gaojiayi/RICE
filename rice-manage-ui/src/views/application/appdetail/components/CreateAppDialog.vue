<script setup lang="ts">
import { ref, watch, toRefs } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import type { UploadProps } from "element-plus";

defineOptions({
  name: "CreateAppDialog"
});

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => {
      return {};
    }
  }
});
const ruleFormRef = ref<FormInstance>();
const imageUrl = ref("");
// dialog 显示开关
const formVisible = ref(false);
const formData = ref(props.data);
const textareaValue = ref("");

const beforeAvatarUpload: UploadProps["beforeUpload"] = rawFile => {
  if (rawFile.type !== "image/jpeg") {
    ElMessage.error("Avatar picture must be JPG format!");
    return false;
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error("Avatar picture size can not exceed 2MB!");
    return false;
  }
  return true;
};
const handleAvatarSuccess: UploadProps["onSuccess"] = (
  response,
  uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!);
};

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(valid => {
    if (valid) {
      ElMessage.success("提交成功");
      formVisible.value = false;
      resetForm(formEl);
    }
  });
};

const closeDialog = () => {
  formVisible.value = false;
  resetForm(ruleFormRef.value);
};
// 这边是更新父组件的visible
const emit = defineEmits(["update:visible"]);

//监听formVisible值的变化
watch(
  () => formVisible.value,
  val => {
    emit("update:visible", val);
  }
);
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

watch(
  () => props.visible,
  val => {
    formVisible.value = val;
  }
);

watch(
  () => props.data,
  val => {
    formData.value = val;
  }
);

const rules = {
  name: [{ required: true, message: "请输入产品名称", trigger: "blur" }]
};
</script>

<template>
  <el-dialog
    v-model="formVisible"
    title="新建应用"
    :width="680"
    draggable
    :before-close="closeDialog"
  >
    <!-- 表单 -->
    <el-form
      ref="ruleFormRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="应用图片" prop="">
        <!-- 应用图片 -->
        <el-upload
          class="avatar-uploader"
          action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <!-- <Plus /> -->
          <el-icon v-else class="avatar-uploader-icon">
            <iconify-icon-offline icon="plus"
          /></el-icon>
        </el-upload>
      </el-form-item>
      <!-- 应用名称 -->

      <el-form-item label="应用名称" prop="name">
        <el-input
          v-model="formData.name"
          :style="{ width: '480px' }"
          placeholder="请输入产品名称"
        />
      </el-form-item>

      <!-- text 应用描述 -->
      <el-form-item label="描述" prop="mark">
        <el-input
          v-model="textareaValue"
          type="textarea"
          :style="{ width: '480px' }"
          placeholder="请输入内容"
        />
      </el-form-item>
    </el-form>
    <!-- 按钮 -->
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button type="primary" @click="submitForm(ruleFormRef)">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.avatar-uploader {
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
}

.avatar-uploader {
  // .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  // }
}

.avatar-uploader {
  .el-upload:hover {
    border-color: var(--el-color-primary);
  }
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
