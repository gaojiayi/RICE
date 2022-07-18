import logDialog from "./LogDialog.vue"
import taskInstanceDataTable from "./TaskInstancedDataTable.vue"
import { App } from "vue";

const LogDialog = Object.assign(logDialog,{install(app: App){
  app.component(logDialog.name, logDialog);

}
});

const TaskInstanceDataTable = Object.assign(taskInstanceDataTable, {
  install(app: App) {
    app.component(taskInstanceDataTable.name, taskInstanceDataTable);
  }
});
export { LogDialog, TaskInstanceDataTable };
