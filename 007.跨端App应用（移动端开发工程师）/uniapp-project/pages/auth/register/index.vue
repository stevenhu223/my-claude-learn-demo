<template>
  <view class="register-page">
    <view class="header">
      <navigator url="/pages/auth/login/index" class="back-btn">←</navigator>
      <view class="header-brand">
        <text class="icon">💰</text>
        <text class="brand-text">财务管家</text>
      </view>
    </view>

    <view class="content">
      <view class="form-card">
        <view v-if="errorMessage" class="error-message">{{ errorMessage }}</view>

        <view class="form-header">
          <view class="header-icon">👤</view>
          <h2 class="form-title">注册到财务管家</h2>
          <p class="form-subtitle">开始您的轻松财务管理之旅</p>
        </view>

        <view class="form-body">
          <view class="input-group">
            <label class="input-label">用户名</label>
            <view class="input-wrapper">
              <text class="input-icon">👤</text>
              <input v-model="username" class="input-field" type="text" placeholder="请输入用户名（3-20个字符）" />
            </view>
          </view>

          <view class="input-group">
            <label class="input-label">昵称（选填）</label>
            <view class="input-wrapper">
              <text class="input-icon">🏷️</text>
              <input v-model="nickname" class="input-field" type="text" placeholder="请输入昵称（不填则使用用户名）" />
            </view>
          </view>

          <view class="input-group">
            <label class="input-label">设置密码</label>
            <view class="input-wrapper">
              <text class="input-icon">🔒</text>
              <input v-model="password" class="input-field" :type="showPassword ? 'text' : 'password'" placeholder="建议包含字母与数字" />
              <view class="password-toggle" @click="showPassword = !showPassword">{{ showPassword ? '👁' : '👁‍🗨' }}</view>
            </view>
          </view>

          <view class="input-group">
            <label class="input-label">确认密码</label>
            <view class="input-wrapper">
              <text class="input-icon">✅</text>
              <input v-model="confirmPassword" class="input-field" :type="showConfirmPassword ? 'text' : 'password'" placeholder="请再次输入密码" />
              <view class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">{{ showConfirmPassword ? '👁' : '👁‍🗨' }}</view>
            </view>
          </view>

          <view class="terms-check">
            <checkbox-group @change="onTermsChange">
              <label class="checkbox-label">
                <checkbox value="agree" :checked="termsAgreed" />
                <text class="checkbox-text">我已阅读并同意 <text class="terms-link">用户协议</text> 和 <text class="terms-link">隐私政策</text></text>
              </label>
            </checkbox-group>
          </view>

          <button class="submit-btn" :disabled="loading" @click="handleRegister">
            <text v-if="!loading">开启智慧理财之旅 →</text>
            <text v-else>注册中...</text>
          </button>

          <view class="login-link">
            <text class="link-text">已有账号？</text>
            <navigator url="/pages/auth/login/index" class="link-btn">去登录</navigator>
          </view>
        </view>
      </view>
    </view>

    <view class="toast" :class="{ 'toast-show': toastShow, 'toast-success': toastType === 'success' }">
      {{ toastMessage }}
    </view>
  </view>
</template>

<script>
import { AuthAPI, Utils } from '@/utils/api.js'

export default {
  data() {
    return {
      username: '',
      nickname: '',
      password: '',
      confirmPassword: '',
      showPassword: false,
      showConfirmPassword: false,
      termsAgreed: false,
      loading: false,
      errorMessage: '',
      toastShow: false,
      toastMessage: '',
      toastType: 'error'
    }
  },
  methods: {
    onTermsChange(e) {
      this.termsAgreed = e.detail.value.length > 0
    },
    showToast(message, type = 'error') {
      this.toastMessage = message
      this.toastType = type
      this.toastShow = true
      setTimeout(() => { this.toastShow = false }, 3000)
    },
    async handleRegister() {
      this.errorMessage = ''
      const usernameError = Utils.validateUsername(this.username)
      if (usernameError) { this.errorMessage = usernameError; return }
      const passwordError = Utils.validatePassword(this.password)
      if (passwordError) { this.errorMessage = passwordError; return }
      if (this.password !== this.confirmPassword) { this.errorMessage = '两次输入的密码不一致'; return }
      if (!this.termsAgreed) { this.errorMessage = '请阅读并同意用户协议和隐私政策'; return }
      this.loading = true
      try {
        const data = await AuthAPI.register(this.username, this.password, this.nickname || this.username)
        if (data.code === 201) {
          this.showToast('注册成功！即将跳转...', 'success')
          setTimeout(() => { uni.reLaunch({ url: '/pages/home/index/index' }) }, 1000)
        } else {
          this.errorMessage = data.message || '注册失败'
        }
      } catch (error) {
        this.errorMessage = error.message || '网络错误，请检查后端服务是否启动'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss">
.register-page {
  min-height: 100vh;
  background: radial-gradient(circle at 10% 20%, rgba(255, 245, 247, 1) 0%, rgba(251, 249, 248, 1) 90%);
}

.header {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
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

  .header-brand {
    display: flex;
    align-items: center;
    gap: 8rpx;

    .icon { font-size: 56rpx; }
    .brand-text { font-size: 40rpx; font-weight: 700; color: #944652; }
  }
}

.content {
  padding: 80rpx 40rpx;
}

.form-card {
  max-width: 480px;
  margin: 0 auto;
  padding: 64rpx;
  background: white;
  border-radius: 48rpx;
  box-shadow: 0 8rpx 30rpx rgba(255, 158, 170, 0.12);
}

.error-message {
  background: #fee2e2;
  border: 1px solid #fecaca;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-bottom: 16rpx;
  color: #dc2626;
  font-size: 14rpx;
}

.form-header {
  text-align: center;
  margin-bottom: 64rpx;

  .header-icon {
    width: 128rpx;
    height: 128rpx;
    background: #ffd9dc;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 32rpx;
    font-size: 64rpx;
  }

  .form-title { font-size: 40rpx; font-weight: 700; color: #1b1c1c; margin-bottom: 16rpx; }
  .form-subtitle { font-size: 14rpx; color: #534344; }
}

.form-body {
  display: flex;
  flex-direction: column;
  gap: 40rpx;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.input-label { font-size: 12rpx; color: #534344; margin-left: 8rpx; }

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f3f3;
  border-radius: 32rpx;
  padding: 0 32rpx;
  height: 104rpx;

  .input-icon { font-size: 40rpx; opacity: 0.4; }
  .input-field { flex: 1; height: 104rpx; font-size: 16rpx; background: transparent; border: none; outline: none; color: #1b1c1c; }
  .password-toggle { padding: 16rpx; font-size: 40rpx; opacity: 0.4; }
}

.terms-check {
  margin-top: 16rpx;

  .checkbox-label {
    display: flex;
    align-items: flex-start;
    gap: 16rpx;
    cursor: pointer;

    .checkbox-text { font-size: 12rpx; color: #534344; line-height: 1.5; }
    .terms-link { color: #944652; }
  }
}

.submit-btn {
  width: 100%;
  height: 112rpx;
  background: #ff9eaa;
  color: white;
  border: none;
  border-radius: 40rpx;
  font-size: 16rpx;
  font-weight: 600;
  margin-top: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(255, 158, 170, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-link {
  text-align: center;
  margin-top: 64rpx;

  .link-text { font-size: 14rpx; color: #534344; }
  .link-btn { color: #944652; font-weight: 700; margin-left: 8rpx; }
}

.toast {
  position: fixed;
  top: 40rpx;
  left: 50%;
  transform: translateX(-50%) translateY(-100rpx);
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  background: #dc2626;
  color: white;
  font-size: 14rpx;
  z-index: 9999;
  transition: transform 0.3s;

  &.toast-show { transform: translateX(-50%) translateY(0); }
  &.toast-success { background: #7DD87D; }
}
</style>