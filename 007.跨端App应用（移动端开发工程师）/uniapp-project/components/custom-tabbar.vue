<template>
  <view class="custom-tabbar" v-if="show">
    <view
      v-for="(item, index) in tabList"
      :key="index"
      class="tab-item"
      :class="{ 'center-tab': index === 2 }"
      @click="switchTab(item, index)"
    >
      <view v-if="index === 2" class="center-icon">+</view>
      <template v-else>
        <image
          class="tab-icon"
          :src="isActive(item.pagePath) ? item.selectedIconPath : item.iconPath"
          mode="aspectFit"
        />
        <text class="tab-text" :class="{ active: isActive(item.pagePath) }">{{ item.text }}</text>
      </template>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CustomTabbar',
  data() {
    return {
      show: true,
      currentPath: '',
      tabList: [
        {
          text: '首页',
          pagePath: '/pages/home/index/index',
          iconPath: '/static/tabbar/home.png',
          selectedIconPath: '/static/tabbar/home-active.png'
        },
        {
          text: '流水',
          pagePath: '/pages/home/records/index',
          iconPath: '/static/tabbar/records.png',
          selectedIconPath: '/static/tabbar/records-active.png'
        },
        {
          text: '记账',
          pagePath: '/pages/home/add-record/index',
          iconPath: '/static/tabbar/add.png',
          selectedIconPath: '/static/tabbar/add-active.png'
        },
        {
          text: '统计',
          pagePath: '/pages/home/statistics/index',
          iconPath: '/static/tabbar/statistics.png',
          selectedIconPath: '/static/tabbar/statistics-active.png'
        },
        {
          text: '我的',
          pagePath: '/pages/home/mine/index',
          iconPath: '/static/tabbar/mine.png',
          selectedIconPath: '/static/tabbar/mine-active.png'
        }
      ]
    }
  },
  mounted() {
    this.updateCurrentPath()
    // 监听页面变化
    uni.onAppRoute(() => {
      this.updateCurrentPath()
    })
  },
  methods: {
    updateCurrentPath() {
      // #ifdef H5
      this.currentPath = window.location.pathname
      // #endif

      // #ifndef H5
      const pages = getCurrentPages()
      if (pages.length) {
        const currentPage = pages[pages.length - 1]
        this.currentPath = '/' + (currentPage.route || currentPage.__route__)
      }
      // #endif
    },
    isActive(path) {
      return this.currentPath && (this.currentPath.includes(path) || this.currentPath.endsWith(path))
    },
    switchTab(item, index) {
      if (this.isActive(item.pagePath)) return

      //记账页面使用 navigateTo（不在 tabBar 中）
      if (index === 2) {
        uni.navigateTo({ url: item.pagePath })
      } else {
        // 其他 tabBar 页面
        // #ifndef H5
        uni.switchTab({ url: item.pagePath })
        // #endif

        // #ifdef H5
        uni.navigateTo({ url: item.pagePath })
        // #endif
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.custom-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 120rpx;
  background: white;
  border-top: 1px solid #f5f3f3;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 99;

  .tab-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4rpx;
    padding: 16rpx 24rpx;
    height: 100rpx;

    .tab-icon {
      width: 44rpx;
      height: 44rpx;
    }

    .tab-text {
      font-size: 20rpx;
      color: #999999;

      &.active {
        color: #944652;
      }
    }

    &.center-tab {
      .center-icon {
        width: 96rpx;
        height: 96rpx;
        background: #944652;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 56rpx;
        color: white;
        margin-top: -40rpx;
        box-shadow: 0 8rpx 24rpx rgba(148, 70, 82, 0.4);
      }
    }
  }
}
</style>