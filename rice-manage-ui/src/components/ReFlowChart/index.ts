import nodePanel from "./src/NodePanel.vue";
import control from "./src/Control.vue";
import dataDialog from "./src/DataDialog.vue"

import { withInstall } from "/@/utils";

/** LogicFlow流程图-拖拽面板 */
const NodePanel = withInstall(nodePanel);

/** LogicFlow流程图-控制面板 */
const ControlPanel = withInstall(control);

/** LogicFlow流程图-控制面板 */
const DataDialog = withInstall(dataDialog);
export { NodePanel, ControlPanel, DataDialog };
