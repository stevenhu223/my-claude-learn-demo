<template>
  <view class="add-record-page">
    <view class="header">
      <view class="back-btn" @click="goBack">←</view>
      <text class="header-title">记一笔</text>
      <view class="placeholder"></view>
    </view>

    <view class="content">
      <view class="amount-section">
        <view class="type-tabs">
          <view class="tab-item" :class="{ active: currentType === 1 }" @click="switchType(1)">支出</view>
          <view class="tab-item" :class="{ active: currentType === 2 }" @click="switchType(2)">收入</view>
        </view>
        <view class="amount-display">
          <text class="currency">¥</text>
          <text class="amount">{{ currentAmount }}</text>
        </view>
        <view class="numpad">
          <view class="num-row">
            <view class="num-key" @click="appendNum('1')">1</view>
            <view class="num-key" @click="appendNum('2')">2</view>
            <view class="num-key" @click="appendNum('3')">3</view>
            <view class="num-key action-key" @click="backspace">⌫</view>
          </view>
          <view class="num-row">
            <view class="num-key" @click="appendNum('4')">4</view>
            <view class="num-key" @click="appendNum('5')">5</view>
            <view class="num-key" @click="appendNum('6')">6</view>
            <view class="num-key action-key" @click="clearAll">C</view>
          </view>
          <view class="num-row">
            <view class="num-key" @click="appendNum('7')">7</view>
            <view class="num-key" @click="appendNum('8')">8</view>
            <view class="num-key" @click="appendNum('9')">9</view>
            <view class="num-key zero-key" @click="appendNum('0')">0</view>
          </view>
          <view class="num-row">
            <view class="num-key" @click="appendNum('.')">.</view>
            <view class="save-btn" @click="saveRecord">保存账单</view>
          </view>
        </view>
      </view>

      <view class="form-section">
        <view class="section-title">选择分类</view>
        <view class="category-grid">
          <view
            v-for="cat in categories"
            :key="cat.id"
            class="category-chip"
            :class="{ active: selectedCategoryId === cat.id }"
            @click="selectCategory(cat)"
          >
            <text class="cat-icon">{{ getCategoryEmoji(cat.icon) }}</text>
            <text class="cat-name">{{ cat.name }}</text>
          </view>
        </view>

        <view class="form-row">
          <view class="form-item">
            <label class="form-label">交易日期</label>
            <picker mode="date" :value="recordDate" @change="onDateChange">
              <view class="picker-value">
                <text class="picker-icon">📅</text>
                <text>{{ recordDate }}</text>
              </view>
            </picker>
          </view>
          <view class="form-item">
            <label class="form-label">支付方式</label>
            <picker mode="selector" :range="paymentMethods" range-key="name" :value="paymentMethodIndex" @change="onPaymentChange">
              <view class="picker-value">
                <text class="picker-icon">💳</text>
                <text>{{ paymentMethods[paymentMethodIndex].name }}</text>
              </view>
            </picker>
          </view>
        </view>

        <view class="form-item">
          <label class="form-label">备注说明</label>
          <textarea v-model="remark" class="remark-input" placeholder="为这笔交易写点什么吧..."></textarea>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { RecordsAPI, CategoriesAPI, Utils } from '@/utils/api.js'

export default {
  data() {
    return {
      currentAmount: '0.00',
      currentType: 1,
      categories: [],
      selectedCategoryId: null,
      recordDate: '',
      paymentMethods: [
        { id: 1, name: '现金' },
        { id: 2, name: '支付宝' },
        { id: 3, name: '微信支付' },
        { id: 4, name: '银行卡' }
      ],
      paymentMethodIndex: 0,
      remark: ''
    }
  },
  onLoad() {
    this.recordDate = Utils.getTodayDateStr()
    this.loadCategories()
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    switchType(type) {
      this.currentType = type
      this.loadCategories()
    },
    async loadCategories() {
      try {
        const data = await CategoriesAPI.getCategories(this.currentType)
        if (data.code === 200 && data.data) {
          this.categories = data.data
          if (data.data.length > 0) {
            this.selectedCategoryId = data.data[0].id
          }
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    appendNum(num) {
      if (this.currentAmount === '0.00') {
        this.currentAmount = num === '.' ? '0.' : num
      } else {
        if (num === '.' && this.currentAmount.includes('.')) return
        if (this.currentAmount.includes('.') && this.currentAmount.split('.')[1].length >= 2) return
        this.currentAmount += num
      }
    },
    backspace() {
      if (this.currentAmount.length <= 1) {
        this.currentAmount = '0.00'
      } else {
        this.currentAmount = this.currentAmount.slice(0, -1)
        if (this.currentAmount.endsWith('.')) this.currentAmount = this.currentAmount.slice(0, -1)
        if (this.currentAmount === '') this.currentAmount = '0.00'
      }
    },
    clearAll() {
      this.currentAmount = '0.00'
    },
    selectCategory(cat) {
      this.selectedCategoryId = cat.id
    },
    onDateChange(e) {
      this.recordDate = e.detail.value
    },
    onPaymentChange(e) {
      this.paymentMethodIndex = e.detail.value
    },
    getCategoryEmoji(icon) { return Utils.getCategoryEmoji(icon) },
    async saveRecord() {
      const amount = parseFloat(this.currentAmount)
      if (amount <= 0 || this.currentAmount === '0.00') {
        uni.showToast({ title: '请输入有效金额', icon: 'none' })
        return
      }
      if (!this.selectedCategoryId) {
        uni.showToast({ title: '请选择分类', icon: 'none' })
        return
      }
      try {
        const data = await RecordsAPI.createRecord({
          type: this.currentType,
          amount: amount,
          categoryId: this.selectedCategoryId,
          recordDate: this.recordDate,
          paymentMethodId: this.paymentMethods[this.paymentMethodIndex].id,
          remark: this.remark
        })
        if (data.code === 200 || data.code === 201) {
          uni.showToast({ title: '保存成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1000)
        } else {
          uni.showToast({ title: data.message || '保存失败', icon: 'none' })
        }
      } catch (error) {
        uni.showToast({ title: '保存失败，请检查网络', icon: 'none' })
      }
    }
  }
}
</script>

<style lang="scss">
.add-record-page {
  min-height: 100vh;
  background: #FFF5F7;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 32rpx;
  background: white;

  .back-btn {
    width: 80rpx;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 48rpx;
    color: #534344;
  }

  .header-title { font-size: 32rpx; font-weight: 600; color: #1b1c1c; }
  .placeholder { width: 80rpx; }
}

.content { padding: 32rpx; }

.amount-section {
  background: white;
  border-radius: 32rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.type-tabs {
  display: flex;
  background: #f5f3f3;
  border-radius: 40rpx;
  padding: 8rpx;
  margin-bottom: 32rpx;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 16rpx;
    border-radius: 32rpx;
    font-size: 28rpx;
    font-weight: 500;
    color: #534344;
    transition: all 0.2s;

    &.active {
      background: #ff9eaa;
      color: white;
      box-shadow: 0 4rpx 12rpx rgba(255, 158, 170, 0.2);
    }
  }
}

.amount-display {
  text-align: center;
  padding: 32rpx 0;

  .currency { font-size: 48rpx; color: #944652; }
  .amount { font-size: 96rpx; font-weight: 700; color: #944652; }
}

.numpad { display: flex; flex-direction: column; gap: 16rpx; }

.num-row {
  display: flex;
  gap: 16rpx;

  .num-key {
    flex: 1;
    height: 96rpx;
    background: #f5f3f3;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40rpx;
    font-weight: 500;
    color: #1b1c1c;

    &:active { background: #ffdae0; }
  }

  .action-key { background: #ff9eaa20; color: #944652; }
  .zero-key { flex: 1; }
  .save-btn {
    flex: 2;
    height: 96rpx;
    background: #944652;
    color: white;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    font-weight: 600;
  }
}

.form-section {
  background: white;
  border-radius: 32rpx;
  padding: 32rpx;

  .section-title { font-size: 28rpx; font-weight: 600; color: #1b1c1c; margin-bottom: 24rpx; }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  margin-bottom: 32rpx;

  .category-chip {
    background: #f5f3f3;
    border-radius: 16rpx;
    padding: 24rpx 16rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8rpx;
    border: 2rpx solid transparent;

    &.active {
      background: rgba(255, 158, 170, 0.3);
      border-color: #ff9eaa;
      box-shadow: 0 4rpx 12rpx rgba(255, 158, 170, 0.2);
    }

    .cat-icon { font-size: 40rpx; }
    .cat-name { font-size: 22rpx; color: #1b1c1c; }
  }
}

.form-row {
  display: flex;
  gap: 24rpx;
  margin-bottom: 24rpx;

  .form-item { flex: 1; }
}

.form-item {
  .form-label { font-size: 24rpx; font-weight: 500; color: #1b1c1c; margin-bottom: 12rpx; display: block; }

  .picker-value {
    display: flex;
    align-items: center;
    gap: 12rpx;
    background: #f5f3f3;
    border-radius: 16rpx;
    padding: 24rpx;
    font-size: 28rpx;
    color: #1b1c1c;

    .picker-icon { font-size: 28rpx; }
  }
}

.remark-input {
  width: 100%;
  height: 160rpx;
  background: #f5f3f3;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #1b1c1c;
  box-sizing: border-box;
}
</style>