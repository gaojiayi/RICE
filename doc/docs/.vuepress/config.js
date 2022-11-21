module.exports = {
  title: 'RICE 新一代分布式调度中间件',
  description: 'RICE 分布式调度中间件',
  head: [ // 注入到当前页面的 HTML <head> 中的标签
    ['link', { rel: 'icon', href: '/favicon.ico' }], // 增加一个自定义的 favicon(网页标签的图标)
  ],
  base: '/', // 这是部署到github相关的配置
  markdown: {
    lineNumbers: false // 代码块显示行号
  },
  themeConfig: {
    nav:[ // 导航栏配置
      {text: '快速入门', link: '/介绍/' },
      {text: 'API', link: '/algorithm/'},
      {text: 'GitHub', link: 'https://github.com/gaojiayi/RICE'}      
    ],
    // sidebar: {
    //   // 侧边栏在 /foo/ 上
    //   '/foo/': [
    //     '',
    //     'one',
    //     'two'
    //   ],
    //   // 侧边栏在 /bar/ 上
    //   '/bar/': [
    //     '',
    //     'three',
    //     'four'
    //   ],
    //   "/":["/bar/","/foo/"]
    // },
    sidebar: [
      {
        title: '介绍',
        collapsable: true, // 能否折叠
        children: [
          ['/introduce/ability/','What RICE can do?'],
          ['/introduce/architecture/','架构设计'],
          ['/introduce/fetures/','关键特性']
          
        ]
      },
      {
        title: '编译部署',
        children: [ /* ... */ ]
      }
    ],
    //sidebar: 'auto', // 侧边栏配置
    sidebarDepth: 2, // 侧边栏显示2级

 

  },
  port: 8180,
  dest: './dist'
};
