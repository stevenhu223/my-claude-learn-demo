<template>
  <view class="records-page">
    <view class="header">
      <view class="header-left">
        <text class="icon">🌸</text>
        <text class="brand">财务管家</text>
      </view>
    </view>

    <view class="search-bar">
      <text class="search-icon">🔍</text>
      <input v-model="searchKeyword" class="search-input" placeholder="搜索账目名称、分类..." @input="onSearch" />
    </view>

    <view class="summary-row">
      <view class="summary-item">
        <text class="summary-label">当月支出</text>
        <text class="summary-value expense">{{ monthExpense }}</text>
      </view>
      <view class="summary-item">
        <text class="summary-label">当月收入</text>
        <text class="summary-value income">{{ monthIncome }}</text>
      </view>
      <view class="summary-item">
        <text class="summary-label">当月结余</text>
        <text class="summary-value" :class="balance >= 0 ? 'income' : 'expense'">{{ monthBalance }}</text>
      </view>
    </view>

    <view class="filter-tabs">
      <view class="tab" :class="{ active: currentFilter === 'all' }" @click="setFilter('all')">全部</view>
      <view class="tab" :class="{ active: currentFilter === 'expense' }" @click="setFilter('expense')">支出</view>
      <view class="tab" :class="{ active: currentFilter === 'income' }" @click="setFilter('income')">收入</view>
    </view>

    <scroll-view class="records-list" scroll-y @scrolltolower="loadMore">
      <view v-for="(records, date) in groupedRecords" :key="date" class="date-group">
        <view class="date-header">
          <view class="date-info">
            <text class="date-day">{{ formatDateDay(date) }}</text>
            <text class="date-weekday">{{ formatDateWeekday(date) }}</text>
          </view>
          <view class="date-summary">
            <text v-if="getDayExpense(records) > 0" class="expense-text">支出 ¥{{ getDayExpense(records).toFixed(2) }}</text>
            <text v-if="getDayIncome(records) > 0" class="income-text">收入 ¥{{ getDayIncome(records).toFixed(2) }}</text>
          </view>
        </view>
        <view v-for="record in records" :key="record.id" class="record-item">
          <view class="record-icon" :class="record.type === 1 ? 'expense-bg' : 'income-bg'">
            <text>{{ getCategoryEmoji(record.category_icon) }}</text>
          </view>
          <view class="record-info">
            <text class="record-name">{{ record.category_name || '未分类' }}{{ record.remark ? ' - ' + record.remark : '' }}</text>
            <view class="record-meta">
              <text class="tag">{{ record.category_name || '未分类' }}</text>
              <text class="time">{{ formatRecordTime(record.created_at) }}</text>
            </view>
          </view>
          <text class="record-amount" :class="record.type === 1 ? 'expense-text' : 'income-text'">
            {{ record.type === 1 ? '-' : '+' }}¥{{ parseFloat(record.amount).toFixed(2) }}
          </text>
        </view>
      </view>
      <view v-if="!Object.keys(groupedRecords).length" class="empty-state">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无记录</text>
      </view>
      <view v-if="hasMore" class="load-more" @click="loadMore">
        <text>加载更多</text>
      </view>
    </scroll-view>

    <navigator url="/pages/home/add-record/index" class="fab">+</navigator>
  </view>
</template>

<script>
import { RecordsAPI, Utils } from '@/utils/api.js'

export default {
  data() {
    return {
      allRecords: [],
      groupedRecords: {},
      currentFilter: 'all',
      searchKeyword: '',
      currentOffset: 0,
      hasMore: true,
      monthExpense: '¥0.00',
      monthIncome: '¥0.00',
      monthBalance: '¥0.00',
      balance: 0
    }
  },
  onLoad() { this.checkLogin() },
  onShow() { this.checkLogin(); this.loadRecords() },
  methods: {
    checkLogin() {
      if (!uni.getStorageSync('finance_buddy_token')) {
        uni.reLaunch({ url: '/pages/auth/login/index' })
      }
    },
    async loadRecords(reset = false) {
      if (reset) this.currentOffset = 0
      const now = new Date()
      const year = now.getFullYear()
      const month = now.getMonth() + 1
      const startDate = `${year}-${String(month).padStart(2, '0')}-01`
      const endDate = new Date(year, month, 0).toISOString().split('T')[0]
      const params = { startDate, endDate, limit: 50, offset: this.currentOffset }
      if (this.currentFilter === 'expense') params.type = 1
      else if (this.currentFilter === 'income') params.type = 2
      try {
        const data = await RecordsAPI.getRecords(params)
        if (data.code === 200) {
          const records = data.data || []
          if (reset) this.allRecords = records
          else this.allRecords = [...this.allRecords, ...records]
          this.groupedRecords = this.groupByDate(this.filterRecords())
          this.hasMore = records.length >= 50
          this.currentOffset += records.length
          this.updateSummary()
        }
      } catch (error) {
        console.error('加载失败:', error)
      }
    },
    filterRecords() {
      if (!this.searchKeyword) return this.allRecords
      return this.allRecords.filter(r =>
        ((r.remark && r.remark.toLowerCase().includes(this.searchKeyword.toLowerCase())) ||
        (r.category_name && r.category_name.toLowerCase().includes(this.searchKeyword.toLowerCase())))
      )
    },
    groupByDate(records) {
      const grouped = {}
      records.forEach(r => {
        const date = r.record_date ? r.record_date.split('T')[0] : ''
        if (!grouped[date]) grouped[date] = []
        grouped[date].push(r)
      })
      return Object.keys(grouped).sort((a, b) => new Date(b) - new Date(a)).reduce((acc, key) => { acc[key] = grouped[key]; return acc }, {})
    },
    updateSummary() {
      const now = new Date()
      const year = now.getFullYear()
      const month = now.getMonth() + 1
      RecordsAPI.getRecords({ startDate: `${year}-${String(month).padStart(2, '0')}-01`, limit: 1000 }).then(data => {
        if (data.code === 200) {
          let exp = 0, inc = 0
          data.data.forEach(r => { r.type === 1 ? exp += parseFloat(r.amount) : inc += parseFloat(r.amount) })
          this.balance = inc - exp
          this.monthExpense = `¥${exp.toFixed(2)}`
          this.monthIncome = `¥${inc.toFixed(2)}`
          this.monthBalance = `¥${this.balance.toFixed(2)}`
        }
      })
    },
    setFilter(filter) {
      this.currentFilter = filter
      this.loadRecords(true)
    },
    onSearch() { this.groupedRecords = this.groupByDate(this.filterRecords()) },
    loadMore() { if (this.hasMore) this.loadRecords(false) },
    getDayExpense(records) { return records.filter(r => r.type === 1).reduce((sum, r) => sum + parseFloat(r.amount), 0) },
    getDayIncome(records) { return records.filter(r => r.type === 2).reduce((sum, r) => sum + parseFloat(r.amount), 0) },
    formatDateDay(date) { const d = new Date(date); return `${d.getMonth() + 1}月${d.getDate()}日` },
    formatDateWeekday(date) { const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']; return weekdays[new Date(date).getDay()] },
    formatRecordTime(dateStr) { if (!dateStr) return ''; const d = new Date(dateStr); return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}` },
    getCategoryEmoji(icon) { return Utils.getCategoryEmoji(icon) }
  }
}
</script>

<style lang="scss">
.records-page {
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
}

.search-bar {
  display: flex;
  align-items: center;
  margin: 24rpx 32rpx;
  padding: 24rpx;
  background: white;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

  .search-icon { font-size: 32rpx; margin-right: 16rpx; }
  .search-input { flex: 1; font-size: 28rpx; background: transparent; border: none; outline: none; color: #1b1c1c; }
}

.summary-row {
  display: flex;
  margin: 0 32rpx 24rpx;
  background: white;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

  .summary-item { flex: 1; text-align: center; }
  .summary-label { font-size: 22rpx; color: #534344; display: block; margin-bottom: 8rpx; }
  .summary-value { font-size: 32rpx; font-weight: 700; }
  .expense { color: #ba1a1a; }
  .income { color: #7DD87D; }
}

.filter-tabs {
  display: flex;
  gap: 16rpx;
  margin: 0 32rpx 24rpx;
  overflow-x: auto;

  .tab {
    padding: 16rpx 32rpx;
    background: white;
    border-radius: 40rpx;
    font-size: 26rpx;
    color: #534344;
    white-space: nowrap;

    &.active { background: #944652; color: white; }
  }
}

.records-list {
  margin: 0 32rpx;
  height: calc(100vh - 600rpx);
}

.date-group { margin-bottom: 32rpx; }

.date-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;

  .date-day { font-size: 32rpx; font-weight: 700; color: #1b1c1c; margin-right: 8rpx; }
  .date-weekday { font-size: 24rpx; color: #999; }
  .date-summary { display: flex; gap: 16rpx; }
  .expense-text { font-size: 24rpx; color: #ba1a1a; }
  .income-text { font-size: 24rpx; color: #7DD87D; }
}

.record-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: white;
  padding: 24rpx;
  border-radius: 20rpx;
  margin-bottom: 16rpx;

  .record-icon {
    width: 88rpx;
    height: 88rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40rpx;

    &.expense-bg { background: rgba(186, 26, 26, 0.08); }
    &.income-bg { background: rgba(125, 216, 125, 0.12); }
  }

  .record-info { flex: 1; }
  .record-name { font-size: 28rpx; color: #1b1c1c; display: block; }
  .record-meta { display: flex; align-items: center; gap: 8rpx; margin-top: 8rpx; }
  .tag { font-size: 22rpx; color: #534344; background: #f5f3f3; padding: 4rpx 12rpx; border-radius: 8rpx; }
  .time { font-size: 22rpx; color: #999; }

  .record-amount { font-size: 28rpx; font-weight: 700; }
  .expense-text { color: #ba1a1a; }
  .income-text { color: #7DD87D; }
}

.empty-state {
  text-align: center;
  padding: 80rpx 0;
  .empty-icon { font-size: 80rpx; display: block; margin-bottom: 16rpx; }
  .empty-text { font-size: 28rpx; color: #534344; }
}

.load-more {
  text-align: center;
  padding: 32rpx;
  color: #944652;
  font-size: 28rpx;
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