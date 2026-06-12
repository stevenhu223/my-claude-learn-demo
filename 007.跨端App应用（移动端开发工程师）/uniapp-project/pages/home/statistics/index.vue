<template>
  <view class="statistics-page">
    <view class="header">
      <view class="header-left">
        <text class="icon">🌸</text>
        <text class="brand">财务管家</text>
      </view>
      <picker mode="date" fields="month" :value="currentDate" @change="onDateChange">
        <view class="date-picker">
          <text class="date-value">{{ year }}-{{ String(month).padStart(2, '0') }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <scroll-view class="content" scroll-y>
      <view class="summary-grid">
        <view class="summary-card income-card">
          <view class="card-header">
            <text class="card-label">本月总收入</text>
            <text class="card-icon">💰</text>
          </view>
          <text class="card-value income">{{ totalIncome }}</text>
          <view class="card-footer">
            <text class="change-text" :class="incomeChange >= 0 ? 'positive' : 'negative'">{{ incomeChange >= 0 ? '+' : '' }}{{ incomeChange }}%</text>
            <text class="change-label">较上月</text>
          </view>
        </view>

        <view class="summary-card expense-card">
          <view class="card-header">
            <text class="card-label">本月总支出</text>
            <text class="card-icon">💸</text>
          </view>
          <text class="card-value expense">{{ totalExpense }}</text>
          <view class="card-footer">
            <text class="change-text" :class="expenseChange >= 0 ? 'positive' : 'negative'">{{ expenseChange >= 0 ? '+' : '' }}{{ expenseChange }}%</text>
            <text class="change-label">较上月</text>
          </view>
        </view>

        <view class="summary-card balance-card">
          <view class="card-header">
            <text class="card-label">本月结余</text>
            <text class="card-icon">📊</text>
          </view>
          <text class="card-value" :class="balance >= 0 ? 'income' : 'expense'">{{ balanceAmount }}</text>
        </view>

        <view class="summary-card count-card">
          <view class="card-header">
            <text class="card-label">交易笔数</text>
            <text class="card-icon">📝</text>
          </view>
          <text class="card-value">{{ transactionCount }}<text class="unit">笔</text></text>
        </view>
      </view>

      <view class="section">
        <view class="section-title">支出分布</view>
        <view class="pie-chart" v-if="expenseCategories.length">
          <view class="pie-placeholder">
            <text class="pie-total">{{ expenseTotal }}</text>
            <text class="pie-label">总支出</text>
          </view>
        </view>
        <view class="category-list">
          <view v-for="(cat, index) in expenseCategories" :key="cat.id" class="category-item">
            <view class="cat-left">
              <text class="cat-emoji">{{ getCategoryEmoji(cat.icon) }}</text>
              <text class="cat-name">{{ cat.name }}</text>
            </view>
            <view class="cat-right">
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: cat.percentage + '%' }"></view>
              </view>
              <text class="cat-amount">¥{{ parseFloat(cat.total).toFixed(2) }}</text>
            </view>
          </view>
        </view>
        <view v-if="!expenseCategories.length" class="empty">暂无数据</view>
      </view>

      <view class="section">
        <view class="section-title">收入分布</view>
        <view class="category-list">
          <view v-for="cat in incomeCategories" :key="cat.id" class="category-item">
            <view class="cat-left">
              <text class="cat-emoji">{{ getCategoryEmoji(cat.icon) }}</text>
              <text class="cat-name">{{ cat.name }}</text>
            </view>
            <view class="cat-right">
              <view class="progress-bar">
                <view class="progress-fill income" :style="{ width: cat.percentage + '%' }"></view>
              </view>
              <text class="cat-amount">¥{{ parseFloat(cat.total).toFixed(2) }}</text>
            </view>
          </view>
        </view>
        <view v-if="!incomeCategories.length" class="empty">暂无数据</view>
      </view>

      <view class="tip-card">
        <text class="tip-icon">💡</text>
        <view class="tip-content">
          <text class="tip-title">理财小贴士</text>
          <text class="tip-text">{{ tipMessage }}</text>
        </view>
      </view>
    </scroll-view>

    <navigator url="/pages/home/add-record/index" class="fab">+</navigator>
  </view>
</template>

<script>
import { StatisticsAPI, Utils } from '@/utils/api.js'

export default {
  data() {
    return {
      year: new Date().getFullYear(),
      month: new Date().getMonth() + 1,
      currentDate: '',
      totalIncome: '¥0.00',
      totalExpense: '¥0.00',
      balance: 0,
      balanceAmount: '¥0.00',
      transactionCount: 0,
      incomeChange: 0,
      expenseChange: 0,
      expenseCategories: [],
      incomeCategories: [],
      expenseTotal: '¥0.00',
      tipMessage: '正在分析您的财务数据...'
    }
  },
  onLoad() { this.currentDate = `${this.year}-${String(this.month).padStart(2, '0')}` },
  onShow() { this.loadData() },
  methods: {
    onDateChange(e) {
      const [year, month] = e.detail.value.split('-')
      this.year = parseInt(year)
      this.month = parseInt(month)
      this.loadData()
    },
    async loadData() {
      try {
        const [summary, expenseData, incomeData] = await Promise.all([
          StatisticsAPI.getSummary(this.year, this.month).catch(() => ({ code: 400 })),
          StatisticsAPI.getCategoryBreakdown(this.year, this.month, 1).catch(() => ({ code: 400 })),
          StatisticsAPI.getCategoryBreakdown(this.year, this.month, 2).catch(() => ({ code: 400 }))
        ])
        if (summary.code === 200) {
          const s = summary.data
          this.totalIncome = `¥${parseFloat(s.total_income).toFixed(2)}`
          this.totalExpense = `¥${parseFloat(s.total_expense).toFixed(2)}`
          this.balance = parseFloat(s.balance)
          this.balanceAmount = `¥${this.balance.toFixed(2)}`
          this.transactionCount = (s.expense_count || 0) + (s.income_count || 0)
          this.incomeChange = parseFloat(s.income_change_pct || 0)
          this.expenseChange = parseFloat(s.expense_change_pct || 0)
        }
        if (expenseData.code === 200 && expenseData.data) {
          this.expenseCategories = expenseData.data.categories || []
          this.expenseTotal = `¥${parseFloat(expenseData.data.total || 0).toFixed(2)}`
        }
        if (incomeData.code === 200 && incomeData.data) {
          this.incomeCategories = incomeData.data.categories || []
        }
        this.updateTip()
      } catch (error) {
        console.error('加载失败:', error)
      }
    },
    updateTip() {
      const expense = parseFloat(this.totalExpense.replace('¥', ''))
      if (this.balance > 0 && expense > 0) {
        const savingRate = (this.balance / (this.balance + expense) * 100).toFixed(1)
        this.tipMessage = `本月储蓄率达${savingRate}%，继续保持！合理控制支出能帮助您更快达成财务目标。`
      } else if (this.balance < 0) {
        this.tipMessage = '本月支出超过收入，建议关注不必要的开支，尝试记录每日消费情况。'
      } else if (parseFloat(this.totalExpense.replace('¥', '')) === 0) {
        this.tipMessage = '本月还没有支出记录，记得记账来跟踪财务状况哦~'
      } else {
        this.tipMessage = '收支平衡，继续保持关注，分析支出结构有助于优化消费习惯。'
      }
    },
    getCategoryEmoji(icon) { return Utils.getCategoryEmoji(icon) }
  }
}
</script>

<style lang="scss">
.statistics-page {
  min-height: 100vh;
  background-color: #fbf9f8;
  padding-bottom: 160rpx;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 40rpx;
  background: white;

  .header-left { display: flex; align-items: center; gap: 16rpx; }
  .icon { font-size: 48rpx; }
  .brand { font-size: 36rpx; font-weight: 700; color: #944652; }

  .date-picker {
    display: flex;
    align-items: center;
    background: #f5f3f3;
    padding: 12rpx 24rpx;
    border-radius: 40rpx;

    .date-value { font-size: 28rpx; font-weight: 600; color: #1b1c1c; margin-right: 8rpx; }
    .picker-arrow { font-size: 20rpx; color: #534344; }
  }
}

.content { padding: 32rpx; }

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.summary-card {
  background: white;
  padding: 32rpx;
  border-radius: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;

    .card-label { font-size: 24rpx; color: #534344; }
    .card-icon { font-size: 32rpx; }
  }

  .card-value {
    font-size: 48rpx;
    font-weight: 700;
    margin-bottom: 8rpx;

    .unit { font-size: 24rpx; font-weight: normal; margin-left: 4rpx; }
    &.income { color: #7DD87D; }
    &.expense { color: #ba1a1a; }
  }

  .card-footer { display: flex; align-items: center; gap: 8rpx; }
  .change-text { font-size: 22rpx; font-weight: 500; &.positive { color: #7DD87D; } &.negative { color: #ba1a1a; } }
  .change-label { font-size: 22rpx; color: #534344; opacity: 0.6; }
}

.section {
  background: white;
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;

  .section-title { font-size: 28rpx; font-weight: 600; color: #1b1c1c; margin-bottom: 24rpx; }
}

.pie-chart {
  display: flex;
  justify-content: center;
  margin-bottom: 24rpx;

  .pie-placeholder {
    width: 200rpx;
    height: 200rpx;
    background: rgba(148, 70, 82, 0.1);
    border-radius: 50%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .pie-total { font-size: 36rpx; font-weight: 700; color: #944652; }
    .pie-label { font-size: 22rpx; color: #534344; }
  }
}

.category-list { display: flex; flex-direction: column; gap: 20rpx; }

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;

  .cat-left { display: flex; align-items: center; gap: 16rpx; }
  .cat-emoji { font-size: 40rpx; }
  .cat-name { font-size: 28rpx; color: #1b1c1c; }

  .cat-right { display: flex; align-items: center; gap: 16rpx; }

  .progress-bar {
    width: 160rpx;
    height: 12rpx;
    background: #f5f3f3;
    border-radius: 6rpx;
    overflow: hidden;

    .progress-fill {
      height: 100%;
      background: #944652;
      border-radius: 6rpx;
      &.income { background: #7DD87D; }
    }
  }

  .cat-amount { font-size: 28rpx; font-weight: 600; color: #1b1c1c; }
}

.empty { text-align: center; padding: 48rpx; color: #534344; font-size: 28rpx; }

.tip-card {
  background: linear-gradient(135deg, #ff9eaa, #ffb6c1, #944652);
  border-radius: 32rpx;
  padding: 32rpx;
  display: flex;
  gap: 24rpx;
  color: white;

  .tip-icon { font-size: 48rpx; }
  .tip-title { font-size: 28rpx; font-weight: 700; display: block; margin-bottom: 8rpx; }
  .tip-text { font-size: 24rpx; opacity: 0.9; line-height: 1.5; }
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