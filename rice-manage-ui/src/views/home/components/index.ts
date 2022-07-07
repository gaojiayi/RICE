import reInfinite from "./Infinite.vue";
import rePie from "./Pie.vue";
import reLine from "./Line.vue"
import reBar from "./Bar.vue"
import { App } from "vue";

const ReInfinite = Object.assign(reInfinite, {
  install(app: App) {
    app.component(reInfinite.name, reInfinite);
  }
});

const RePie = Object.assign(rePie, {
  install(app: App) {
    app.component(rePie.name, rePie);
  }
});

const ReLine = Object.assign(reLine, {
  install(app: App) {
    app.component(reLine.name, reLine);
  }
});

const ReBar = Object.assign(reBar, {
  install(app: App) {
    app.component(reBar.name, reBar);
  }
});

export { ReInfinite, RePie, ReLine, ReBar };
