<template>
  <view class="home-page">
    <view class="header">
      <view class="header-left">
        <text class="icon">🌸</text>
        <text class="brand">财务管家</text>
      </view>
      <view class="header-right">
        <view class="user-info" @click="goToMine">
          <view class="avatar">{{ userInitial }}</view>
          <text class="username">{{ userNickname }}</text>
        </view>
        <button class="logout-btn" @click="handleLogout">退出</button>
      </view>
    </view>

    <view class="main-content">
      <view class="stats-grid">
        <view class="stat-card expense-card">
          <view class="stat-header">
            <view class="stat-icon expense-icon">📉</view>
            <text class="stat-label">本月支出</text>
          </view>
          <text class="stat-value expense-value">{{ monthExpense }}</text>
          <text class="stat-count">{{ expenseCount }} 笔支出</text>
        </view>

        <view class="stat-card income-card">
          <view class="stat-header">
            <view class="stat-icon income-icon">📈</view>
            <text class="stat-label">本月收入</text>
          </view>
          <text class="stat-value income-value">{{ monthIncome }}</text>
          <text class="stat-count">{{ incomeCount }} 笔收入</text>
        </view>

        <view class="stat-card balance-card">
          <view class="stat-header">
            <view class="stat-icon balance-icon">💰</view>
            <text class="stat-label">本月结余</text>
          </view>
          <text class="stat-value" :class="balance >= 0 ? 'income-value' : 'expense-value'">{{ monthBalance }}</text>
          <text class="stat-count">收入 - 支出</text>
        </view>

        <view class="stat-card today-card primary-card">
          <view class="stat-header">
            <view class="stat-icon">📅</view>
            <text class="stat-label white-label">今日速览</text>
          </view>
          <text class="stat-value white-value">{{ todayBalance }}</text>
          <view class="today-footer">
            <text class="white-text">支出 {{ todayExpense }}</text>
            <navigator url="/pages/home/add-record/index" class="add-btn">
              <text>+</text>
              <text class="add-text">记一笔</text>
            </navigator>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="section-header">
          <view class="section-title-wrapper">
            <view class="title-indicator"></view>
            <text class="section-title">常用分类</text>
          </view>
          <navigator url="/pages/home/statistics/index" class="section-link">
            <text>查看详情 ›</text>
          </navigator>
        </view>
        <view class="categories-grid">
          <view v-for="cat in categories" :key="cat.id" class="category-item" @click="goToAddRecord(cat)">
            <text class="category-icon">{{ getCategoryEmoji(cat.icon) }}</text>
            <text class="category-name">{{ cat.name }}</text>
            <text class="category-amount">{{ formatMoneyShort(cat.total) }}</text>
          </view>
          <view v-if="!categories.length" class="category-empty">
            <text>暂无分类数据</text>
          </view>
        </view>
      </view>

      <view class="section records-section">
        <view class="section-header">
          <text class="section-title">最近记录</text>
          <navigator url="/pages/home/records/index" class="view-all-btn">查看全部</navigator>
        </view>
        <view class="records-list">
          <view v-for="record in recentRecords" :key="record.id" class="record-item" @click="goToRecords">
            <view class="record-icon" :class="record.type === 1 ? 'expense-bg' : 'income-bg'">
              <text>{{ getCategoryEmoji(record.category_icon) }}</text>
            </view>
            <view class="record-info">
              <text class="record-name">{{ record.remark || record.category_name || '未知' }}</text>
              <text class="record-meta">{{ formatRecordDate(record.record_date) }} · {{ record.category_name }}</text>
            </view>
            <text class="record-amount" :class="record.type === 1 ? 'expense-text' : 'income-text'">
              {{ record.type === 1 ? '-' : '+' }}{{ formatMoney(record.amount) }}
            </text>
          </view>
          <view v-if="!recentRecords.length" class="records-empty">
            <view class="empty-hint">
              <text>最近7天暂无记录</text>
              <navigator url="/pages/home/add-record/index" class="empty-link">去记一笔 ›</navigator>
            </view>
          </view>
        </view>
      </view>
    </view>

    <navigator url="/pages/home/add-record/index" class="fab">+</navigator>
  </view>
</template>

<script>
import { AuthAPI, RecordsAPI, StatisticsAPI, Utils } from '@/utils/api.js'

export default {
  data() {
    return {
      userNickname: '用户',
      userInitial: '用',
      monthExpense: '¥ 0.00',
      monthIncome: '¥ 0.00',
      monthBalance: '¥ 0.00',
      balance: 0,
      expenseCount: 0,
      incomeCount: 0,
      todayBalance: '¥ 0.00',
      todayExpense: '¥ 0.00',
      categories: [],
      recentRecords: []
    }
  },
  onLoad() { this.checkLogin() },
  onShow() { this.checkLogin(); this.initData() },
  methods: {
    checkLogin() {
      if (!AuthAPI.isLoggedIn()) {
        uni.reLaunch({ url: '/pages/auth/login/index' })
        return
      }
      this.updateUserInfo()
    },
    updateUserInfo() {
      const user = AuthAPI.getUser()
      if (user) {
        this.userNickname = user.nickname || user.username || '用户'
        this.userInitial = this.userNickname.charAt(0).toUpperCase()
      }
    },
    async initData() {
      const now = new Date()
      const year = now.getFullYear()
      const month = now.getMonth() + 1
      try {
        const [summaryData, todayData, categoryData, recordsData] = await Promise.all([
          StatisticsAPI.getSummary(year, month).catch(() => ({ code: 400 })),
          StatisticsAPI.getToday().catch(() => ({ code: 400 })),
          StatisticsAPI.getCategoryBreakdown(year, month, 1).catch(() => ({ code: 400 })),
          RecordsAPI.getRecords({ limit: 50 }).catch(() => ({ code: 400 }))
        ])
        if (summaryData.code === 200) {
          const s = summaryData.data
          this.monthExpense = Utils.formatMoney(s.total_expense)
          this.monthIncome = Utils.formatMoney(s.total_income)
          this.balance = s.total_income - s.total_expense
          this.monthBalance = Utils.formatMoney(this.balance)
          this.expenseCount = s.expense_count || 0
          this.incomeCount = s.income_count || 0
        }
        if (todayData.code === 200) {
          this.todayBalance = Utils.formatMoney(todayData.data.balance)
          this.todayExpense = Utils.formatMoney(todayData.data.expense)
        }
        if (categoryData.code === 200 && categoryData.data) {
          this.categories = categoryData.data.categories || []
        }
        if (recordsData.code === 200 && recordsData.data) {
          this.recentRecords = this.filterRecentRecords(recordsData.data)
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    filterRecentRecords(records) {
      const last7Days = []
      for (let i = 6; i >= 0; i--) {
        const d = new Date()
        d.setDate(d.getDate() - i)
        const year = d.getFullYear()
        const month = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        last7Days.push(`${year}-${month}-${day}`)
      }
      return records.filter(r => {
        if (!r.record_date) return false
        const recordDate = r.record_date.split('T')[0]
        return last7Days.includes(recordDate)
      }).slice(0, 5)
    },
    formatMoney(amount) { return Utils.formatMoney(amount) },
    formatMoneyShort(amount) { return Utils.formatMoneyShort(amount) },
    formatRecordDate(dateStr) { return Utils.formatRecordDate(dateStr) },
    getCategoryEmoji(icon) { return Utils.getCategoryEmoji(icon) },
    goToMine() { uni.navigateTo({ url: '/pages/home/mine/index' }) },
    goToRecords() { uni.switchTab({ url: '/pages/home/records/index' }) },
    goToAddRecord(cat) { uni.navigateTo({ url: '/pages/home/add-record/index' }) },
    async handleLogout() {
      await AuthAPI.logout()
      uni.reLaunch({ url: '/pages/auth/login/index' })
    }
  }
}
</script>

<style lang="scss">
.home-page {
  min-height: 100vh;
  background-color: #fbf9f8;
  padding-bottom: 120rpx;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 40rpx;
  background: white;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16rpx;
    .icon { font-size: 48rpx; }
    .brand { font-size: 36rpx; font-weight: 700; color: #944652; }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 24rpx;

    .user-info {
      display: flex;
      align-items: center;
      gap: 16rpx;

      .avatar {
        width: 64rpx;
        height: 64rpx;
        background: #ff9eaa;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28rpx;
        font-weight: 600;
        color: white;
      }

      .username { font-size: 28rpx; color: #1b1c1c; }
    }

    .logout-btn {
      padding: 12rpx 24rpx;
      font-size: 24rpx;
      color: #ba1a1a;
      background: #fee2e2;
      border-radius: 16rpx;
      border: none;
    }
  }
}

.main-content { padding: 48rpx 32rpx; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32rpx;
  margin-bottom: 64rpx;
}

.stat-card {
  background: white;
  padding: 40rpx;
  border-radius: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .stat-header {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-bottom: 24rpx;

    .stat-icon {
      width: 64rpx;
      height: 64rpx;
      border-radius: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32rpx;
    }

    .expense-icon { background: #fee2e2; }
    .income-icon { background: rgba(125, 216, 125, 0.12); }
    .balance-icon { background: rgba(148, 70, 82, 0.1); }

    .stat-label { font-size: 24rpx; color: #534344; }
    .white-label { color: rgba(255, 255, 255, 0.8); }
  }

  .stat-value { font-size: 48rpx; font-weight: 700; margin-bottom: 8rpx; }
  .expense-value { color: #ba1a1a; }
  .income-value { color: #7DD87D; }
  .stat-count { font-size: 22rpx; color: #534344; opacity: 0.6; }

  &.primary-card {
    background: linear-gradient(135deg, #944652, #ff9eaa);

    .white-value { color: white; }

    .today-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 16rpx;

      .white-text { font-size: 24rpx; color: rgba(255, 255, 255, 0.7); }

      .add-btn {
        display: flex;
        align-items: center;
        gap: 8rpx;
        padding: 12rpx 24rpx;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 40rpx;
        font-size: 24rpx;
        color: white;
      }
    }
  }
}

.section { margin-bottom: 64rpx; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;

  .section-title-wrapper {
    display: flex;
    align-items: center;
    gap: 16rpx;

    .title-indicator {
      width: 8rpx;
      height: 40rpx;
      background: #944652;
      border-radius: 4rpx;
    }

    .section-title { font-size: 32rpx; font-weight: 700; color: #1b1c1c; }
  }

  .section-link { font-size: 24rpx; color: #944652; }
  .view-all-btn { padding: 16rpx 32rpx; background: #944652; color: white; font-size: 24rpx; border-radius: 16rpx; }
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx;

  .category-item {
    background: white;
    padding: 32rpx;
    border-radius: 24rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16rpx;
    box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

    .category-icon { font-size: 56rpx; }
    .category-name { font-size: 24rpx; font-weight: 500; color: #1b1c1c; }
    .category-amount { font-size: 22rpx; color: #534344; opacity: 0.6; }
  }

  .category-empty { grid-column: span 3; text-align: center; padding: 64rpx; color: #534344; }
}

.records-section {
  background: white;
  border-radius: 48rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .records-list { display: flex; flex-direction: column; gap: 32rpx; }

  .record-item {
    display: flex;
    align-items: center;
    gap: 24rpx;
    padding: 24rpx 0;
    border-bottom: 1px solid #f5f3f3;

    &:last-child { border-bottom: none; }

    .record-icon {
      width: 88rpx;
      height: 88rpx;
      border-radius: 24rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 40rpx;

      &.expense-bg { background: rgba(186, 26, 26, 0.08); }
      &.income-bg { background: rgba(125, 216, 125, 0.12); }
    }

    .record-info {
      flex: 1;
      .record-name { font-size: 28rpx; font-weight: 500; color: #1b1c1c; display: block; }
      .record-meta { font-size: 24rpx; color: #534344; opacity: 0.6; }
    }

    .record-amount { font-size: 28rpx; font-weight: 700; }
    .expense-text { color: #ba1a1a; }
    .income-text { color: #7DD87D; }
  }

  .records-empty {
    padding: 64rpx;

    .empty-hint {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16rpx;
      color: #534344;
    }

    .empty-link {
      color: #944652;
      font-size: 28rpx;
      font-weight: 500;
    }
  }
}

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 180rpx;
  width: 100rpx;
  height: 100rpx;
   background: linear-gradient(135deg, #ff9eaa 0%, #944652 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56rpx;
  font-weight: 300;
  box-shadow:
    0 4rpx 12rpx rgba(148, 70, 82, 0.25),
    0 8rpx 24rpx rgba(148, 70, 82, 0.15),
    inset 0 2rpx 4rpx rgba(255, 255, 255, 0.2);
  z-index: 100;
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.95);
  }
}

</style>