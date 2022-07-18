import { h, defineComponent, withDirectives, resolveDirective } from "vue";

// 1、resolveDirective
//     如果在当前应用实例中可用,则允许通过其名称解析一个directive。
//     返回一个Directive,如果没有找到,则返回undefined
//     只能在render或setup函数中使用。

// 2、withDirectives
//     允许将指令应用于VNode,返回一个包含应用指令的VNode。
//     withDirectives只能在render或setup函数中使用。

// 封装@vueuse/motion动画库中的自定义指令v-motion   https://www.npmjs.com/package/@vueuse/motion
export default defineComponent({
  name: "Motion",
  props: {
    delay: {
      type: Number,
      default: 50
    }
  },
  render() {
    const { delay } = this;
    const motion = resolveDirective("motion");
    return withDirectives(
      // h创建节点, 可实现展示template如何渲染到html中得过程，因为vue渲染到页面上是通过loader打包成模板字符串拼接渲染得，
      // 所以 h 函数同样也是通过字符串渲染到html中

      // 第一个参数 节点类型 div为dom原生节点，需要通过字符串"div"来标识
      // 第二个参数 节点属性 div节点得属性
      // 第三个参数 节点的孩子节点 内部节点(子内容)

      h(
        "div",
        {},
        {
          default: () => [this.$slots.default()]
        }
      ),
      [
        [
          motion,
          {
            initial: { opacity: 0, y: 100 },
            enter: {
              opacity: 1,
              y: 0,
              transition: {
                delay
              }
            }
          }
        ]
      ]
    );
  }
});
