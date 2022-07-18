export const nodeList = [
  {
    text: "开始",
    type: "start",
    class: "node-start"
  },
  {
    text: "矩形",
    type: "rect",
    class: "node-rect"
  },
  {
    type: "user",
    text: "用户",
    class: "node-user"
  },
  {
    type: "push",
    text: "推送",
    class: "node-push"
  },
  {
    type: "download",
    text: "位置",
    class: "node-download"
  },
  {
    type: "end",
    text: "结束",
    class: "node-end"
  }
];

export const BpmnNode = [
  {
    type: "bpmn:startEvent",
    text: "开始",
    class: "bpmn-start"
  },
  {
    type: "bpmn:endEvent",
    text: "结束",
    class: "bpmn-end"
  },
  {
    type: "polygon", // 决定了拖拽形状  如果使用 bpmn:jugement这样的名字则需要注册 参考https://github.com/didi/LogicFlow/blob/master/examples/src/pages/usage/approve/components/registerNode.ts
    text: "条件",
    class: "bpmn-exclusiveGateway"
  },
  {
    type: "rect",
    text: "rice任务",
    class: "bpmn-rice-task", // 这个类名  后面定义icon的样式
  }
];
