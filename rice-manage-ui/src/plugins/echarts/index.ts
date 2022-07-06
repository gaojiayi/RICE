import * as echarts from "echarts/core";

import { PieChart, BarChart, LineChart, GaugeChart } from "echarts/charts";
import { SVGRenderer } from "echarts/renderers";
const { use, registerTheme } = echarts;

import {
  GridComponent,
  TitleComponent,
  LegendComponent,
  ToolboxComponent,
  TooltipComponent,
  DataZoomComponent,
  VisualMapComponent,
} from "echarts/components";

use([
  PieChart,
  BarChart,
  LineChart,
  GaugeChart,
  SVGRenderer,
  GridComponent,
  TitleComponent,
  LegendComponent,
  ToolboxComponent,
  TooltipComponent,
  DataZoomComponent,
  VisualMapComponent
]);
// 自定义主题
import theme from "./theme.json";
registerTheme("ovilia-green", theme);

export default echarts;
