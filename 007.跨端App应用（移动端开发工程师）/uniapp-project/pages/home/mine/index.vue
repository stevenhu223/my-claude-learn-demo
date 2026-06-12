<template>
  <view class="mine-page">
    <view class="header">
      <view class="header-left">
        <text class="icon">🌸</text>
        <text class="brand">财务管家</text>
      </view>
    </view>

    <view class="profile-section">
      <view class="avatar-wrapper" @click="chooseAvatar">
        <view class="avatar" v-if="!avatarUrl">{{ userInitial }}</view>
        <image v-else class="avatar-img" :src="avatarUrl" mode="aspectFill" />
        <view class="avatar-edit">📷</view>
      </view>
      <text class="profile-name">{{ userNickname }}</text>
      <text class="profile-days">理财第 {{ days }} 天</text>
    </view>

    <view class="stats-row">
      <view class="stat-item">
        <text class="stat-value">{{ recordCount }}<text class="stat-unit">笔</text></text>
        <text class="stat-label">本月记账</text>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-value income">{{ checkinDays }}<text class="stat-unit">天</text></text>
        <text class="stat-label">连续打卡</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-card">
        <view class="menu-title">
          <text class="title-icon">💰</text>
          <text>预算管理</text>
        </view>
        <view class="menu-item" @click="openBudgetModal">
          <view class="item-left">
            <text class="item-icon">💳</text>
            <view class="item-content">
              <text class="item-name">月度总预算</text>
              <text class="item-value">{{ budgetDisplay }}</text>
            </view>
          </view>
          <text class="item-arrow">›</text>
        </view>
        <view class="menu-item" @click="toggleOverspendAlert">
          <view class="item-left">
            <text class="item-icon">🔔</text>
            <view class="item-content">
              <text class="item-name">超支提醒</text>
              <text class="item-desc">预算 80% 时提醒</text>
            </view>
          </view>
          <switch :checked="overspendAlertEnabled" @change="toggleOverspendAlert" color="#FF9800" />
        </view>
      </view>

      <view class="menu-card">
        <view class="menu-title">
          <text class="title-icon">⚙️</text>
          <text>系统设置</text>
        </view>
        <view class="menu-item" @click="handleLogout">
          <view class="item-left">
            <text class="item-icon logout-icon">🚪</text>
            <view class="item-content">
              <text class="item-name logout-text">退出登录</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="footer">
      <text class="version">财务管家 V1.0.1</text>
    </view>

    <!-- 预算弹窗 -->
    <view class="modal" v-if="showBudgetModal">
      <view class="modal-mask" @click="closeBudgetModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">月度总预算</text>
          <text class="modal-close" @click="closeBudgetModal">✕</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <label class="form-label">选择月份</label>
            <picker mode="date" fields="month" :value="budgetYm" @change="onBudgetMonthChange">
              <view class="picker-value">{{ budgetYm }}</view>
            </picker>
          </view>
          <view class="form-item">
            <label class="form-label">预算金额（元）</label>
            <input v-model="budgetAmount" type="digit" class="amount-input" placeholder="请输入预算金额" />
          </view>
          <view v-if="budgetError" class="error-msg">{{ budgetError }}</view>
        </view>
        <view class="modal-footer">
          <button class="btn-cancel" @click="closeBudgetModal">取消</button>
          <button class="btn-save" @click="saveBudget">保存</button>
        </view>
      </view>
    </view>

    <!-- 超支提醒 Toast -->
    <view class="overspend-toast" :class="{ show: showOverspendToast }">
      <text class="toast-icon">⚠️</text>
      <text class="toast-text">{{ overspendMessage }}</text>
    </view>
  </view>
</template>

<script>
import { AuthAPI, BudgetAPI, StatisticsAPI } from '@/utils/api.js'

export default {
  data() {
    return {
      userNickname: '用户',
      userInitial: '用',
      avatarUrl: '',
      days: 365,
      recordCount: 0,
      checkinDays: 0,
      budgetDisplay: '¥0.00',
      overspendAlertEnabled: false,
      showBudgetModal: false,
      budgetYm: '',
      budgetAmount: '',
      budgetError: '',
      showOverspendToast: false,
      overspendMessage: ''
    }
  },
  onLoad() { this.checkLogin() },
  onShow() { this.checkLogin(); this.loadUserInfo(); this.loadBudget() },
  methods: {
    checkLogin() {
      if (!AuthAPI.isLoggedIn()) {
        uni.reLaunch({ url: '/pages/auth/login/index' })
      }
    },
    loadUserInfo() {
      const user = AuthAPI.getUser()
      if (user) {
        this.userNickname = user.nickname || user.username || '用户'
        this.userInitial = this.userNickname.charAt(0).toUpperCase()
      }
      const savedAvatar = uni.getStorageSync('userAvatar')
      if (savedAvatar) this.avatarUrl = savedAvatar
      this.overspendAlertEnabled = uni.getStorageSync('overspendAlertEnabled') === 'true'
    },
    async loadBudget() {
      const now = new Date()
      const ym = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
      try {
        const data = await BudgetAPI.getBudget(ym)
        if (data.code === 200 && data.data && data.data.budgetAmount) {
          this.budgetDisplay = `¥${parseFloat(data.data.budgetAmount).toFixed(2)}`
        }
      } catch (e) {}
    },
    openBudgetModal() {
      const now = new Date()
      this.budgetYm = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
      this.budgetAmount = ''
      this.budgetError = ''
      this.showBudgetModal = true
      this.loadCurrentBudget()
    },
    closeBudgetModal() { this.showBudgetModal = false },
    onBudgetMonthChange(e) { this.budgetYm = e.detail.value },
    async loadCurrentBudget() {
      try {
        const data = await BudgetAPI.getBudget(this.budgetYm)
        if (data.code === 200 && data.data && data.data.budgetAmount) {
          this.budgetAmount = data.data.budgetAmount
        }
      } catch (e) {}
    },
    async saveBudget() {
      this.budgetError = ''
      if (!this.budgetYm) { this.budgetError = '请选择月份'; return }
      const amount = parseFloat(this.budgetAmount)
      if (isNaN(amount) || amount < 0) { this.budgetError = '请输入有效的预算金额'; return }
      try {
        const data = await BudgetAPI.saveBudget(this.budgetYm, amount)
        if (data.code === 200) {
          this.budgetDisplay = `¥${amount.toFixed(2)}`
          this.closeBudgetModal()
          if (this.overspendAlertEnabled) this.checkOverspend()
        } else {
          this.budgetError = data.message || '保存失败'
        }
      } catch (e) { this.budgetError = '网络错误' }
    },
    toggleOverspendAlert() {
      this.overspendAlertEnabled = !this.overspendAlertEnabled
      uni.setStorageSync('overspendAlertEnabled', this.overspendAlertEnabled)
      if (this.overspendAlertEnabled) {
        const now = new Date()
        this.checkOverspend(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`)
      }
    },
    async checkOverspend(ym) {
      try {
        const budgetData = await BudgetAPI.getBudget(ym)
        if (budgetData.code !== 200 || !budgetData.data || !budgetData.data.budgetAmount) return
        const budget = parseFloat(budgetData.data.budgetAmount)
        const [year, month] = ym.split('-').map(Number)
        const summaryData = await StatisticsAPI.getSummary(year, month)
        if (summaryData.code !== 200) return
        const expense = parseFloat(summaryData.data.total_expense || 0)
        const percentage = (expense / budget) * 100
        if (percentage >= 80) {
          this.overspendMessage = `${ym.replace('-', '年')}月已花费 ¥${expense.toFixed(2)}，超过预算的 ${percentage.toFixed(0)}%！`
          this.showOverspendToast = true
          setTimeout(() => { this.showOverspendToast = false }, 5000)
        }
      } catch (e) {}
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.avatarUrl = res.tempFilePaths[0]
          uni.setStorageSync('userAvatar', res.tempFilePaths[0])
        }
      })
    },
    async handleLogout() {
      await AuthAPI.logout()
      uni.reLaunch({ url: '/pages/auth/login/index' })
    }
  }
}
</script>

<style lang="scss">
.mine-page {
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

.profile-section {
  text-align: center;
  padding: 48rpx 0;

  .avatar-wrapper {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin: 0 auto 24rpx;

    .avatar {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, #ff9eaa, #ffb6c1);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 64rpx;
      font-weight: 700;
      color: white;
    }

    .avatar-img {
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }

    .avatar-edit {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 48rpx;
      height: 48rpx;
      background: #944652;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24rpx;
      color: white;
      border: 4rpx solid white;
    }
  }

  .profile-name { font-size: 36rpx; font-weight: 700; color: #1b1c1c; display: block; margin-bottom: 8rpx; }
  .profile-days { font-size: 24rpx; color: #534344; }
}

.stats-row {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 64rpx 48rpx;
  padding: 32rpx;
  background: white;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .stat-item { flex: 1; text-align: center; }
  .stat-value { font-size: 48rpx; font-weight: 700; color: #944652; display: block; margin-bottom: 8rpx; .stat-unit { font-size: 24rpx; font-weight: normal; margin-left: 4rpx; } &.income { color: #7DD87D; } }
  .stat-label { font-size: 22rpx; color: #534344; }
  .stat-divider { width: 2rpx; height: 80rpx; background: #f5f3f3; }
}

.menu-section { padding: 0 32rpx; }

.menu-card {
  background: white;
  border-radius: 24rpx;
  margin-bottom: 32rpx;
  overflow: hidden;

  .menu-title {
    display: flex;
    align-items: center;
    gap: 12rpx;
    padding: 24rpx 32rpx;
    border-bottom: 1px solid #f5f3f3;
    font-size: 28rpx;
    font-weight: 600;
    color: #1b1c1c;
    .title-icon { font-size: 32rpx; }
  }

  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 28rpx 32rpx;
    border-bottom: 1px solid #f5f3f3;
    &:last-child { border-bottom: none; }

    .item-left { display: flex; align-items: center; gap: 20rpx; }
    .item-icon { font-size: 40rpx; }
    .item-content { display: flex; flex-direction: column; gap: 4rpx; }
    .item-name { font-size: 28rpx; color: #1b1c1c; }
    .item-value { font-size: 24rpx; color: #534344; }
    .item-desc { font-size: 24rpx; color: #999; }
    .item-arrow { font-size: 32rpx; color: #999; }
    .logout-icon { color: #ba1a1a; }
    .logout-text { color: #ba1a1a; }
  }
}

.footer { text-align: center; padding: 48rpx 0; .version { font-size: 24rpx; color: #999; opacity: 0.4; } }

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;

  .modal-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
  }

  .modal-content {
    position: relative;
    width: 90%;
    max-width: 600rpx;
    background: white;
    border-radius: 32rpx;
    overflow: hidden;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;
    border-bottom: 1px solid #f5f3f3;

    .modal-title { font-size: 32rpx; font-weight: 700; color: #1b1c1c; }
    .modal-close { font-size: 32rpx; color: #999; padding: 8rpx; }
  }

  .modal-body { padding: 32rpx; }

  .form-item { margin-bottom: 24rpx; }
  .form-label { font-size: 24rpx; color: #534344; display: block; margin-bottom: 12rpx; }
  .picker-value, .amount-input {
    width: 100%;
    padding: 24rpx;
    background: #f5f3f3;
    border-radius: 16rpx;
    font-size: 28rpx;
    color: #1b1c1c;
    box-sizing: border-box;
  }

  .error-msg { color: #ba1a1a; font-size: 24rpx; margin-top: 16rpx; }

  .modal-footer {
    display: flex;
    gap: 24rpx;
    padding: 24rpx 32rpx;
    border-top: 1px solid #f5f3f3;

    button { flex: 1; height: 88rpx; border-radius: 16rpx; font-size: 28rpx; border: none; }
    .btn-cancel { background: #f5f3f3; color: #534344; }
    .btn-save { background: #944652; color: white; }
  }
}

.overspend-toast {
  position: fixed;
  top: 120rpx;
  left: 50%;
  transform: translateX(-50%) translateY(-200rpx);
  background: #FF9800;
  color: white;
  padding: 24rpx 32rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  z-index: 9999;
  transition: transform 0.3s;

  &.show { transform: translateX(-50%) translateY(0); }
  .toast-icon { font-size: 32rpx; }
  .toast-text { font-size: 26rpx; }
}

</style>