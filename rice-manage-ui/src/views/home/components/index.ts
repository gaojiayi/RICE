import reInfinite from "./Infinite.vue";
import rePie from "./Pie.vue";
import reLine from "./Line.vue"
import reClock from "./Clock.vue"
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

const ReClock = Object.assign(reClock, {
  install(app: App) {
    app.component(reClock.name, reClock);
  }
});

export { ReInfinite, RePie, ReLine, ReClock };
