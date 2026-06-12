<template>
  <view class="login-page">
    <view class="brand-section" v-if="!isMobile">
      <view class="brand-icon">💰</view>
      <h1 class="brand-title">财务管家</h1>
      <p class="brand-desc">遇见更有温度的理财生活<br>让每一分钱都有它温柔的归宿</p>
    </view>

    <view class="form-section">
      <view class="mobile-logo" v-if="isMobile">
        <view class="logo-icon">💰</view>
        <h2>财务管家</h2>
      </view>

      <view class="form-header">
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">请登录您的账户以管理您的财务</p>
      </view>

      <view v-if="errorMessage" class="error-message">{{ errorMessage }}</view>

      <view class="form-body">
        <view class="input-group">
          <label class="input-label">用户名</label>
          <view class="input-wrapper">
            <text class="input-icon">👤</text>
            <input v-model="username" class="input-field" type="text" placeholder="请输入用户名" />
          </view>
        </view>

        <view class="input-group">
          <label class="input-label">登录密码</label>
          <view class="input-wrapper">
            <text class="input-icon">🔒</text>
            <input v-model="password" class="input-field" :type="showPassword ? 'text' : 'password'" placeholder="请输入您的密码" />
            <view class="password-toggle" @click="showPassword = !showPassword">{{ showPassword ? '👁' : '👁‍🗨' }}</view>
          </view>
        </view>

        <view class="form-options">
          <label class="remember-me">
            <checkbox-group @change="onRememberChange">
              <checkbox value="remember" :checked="rememberMe" />
              <text>记住我</text>
            </checkbox-group>
          </label>
          <navigator url="/pages/auth/register/index" class="forgot-link">忘记密码？</navigator>
        </view>

        <button class="submit-btn" :disabled="loading" @click="handleLogin">
          <text v-if="!loading">立即登录</text>
          <text v-else>登录中...</text>
        </button>

        <view class="register-link">
          <text>还没有账号？</text>
          <navigator url="/pages/auth/register/index" class="link-btn">立即注册</navigator>
        </view>
      </view>
    </view>

    <view class="toast" :class="{ 'toast-show': toastShow, 'toast-success': toastType === 'success' }">
      {{ toastMessage }}
    </view>
  </view>
</template>

<script>
import { AuthAPI } from '@/utils/api.js'

export default {
  data() {
    return {
      username: '',
      password: '',
      showPassword: false,
      rememberMe: false,
      loading: false,
      errorMessage: '',
      toastShow: false,
      toastMessage: '',
      toastType: 'error'
    }
  },
  computed: {
    isMobile() {
      return true
    }
  },
  onLoad() {
    if (AuthAPI.isLoggedIn()) {
      uni.reLaunch({ url: '/pages/home/index/index' })
    }
  },
  methods: {
    onRememberChange(e) {
      this.rememberMe = e.detail.value.length > 0
    },
    showToast(message, type = 'error') {
      this.toastMessage = message
      this.toastType = type
      this.toastShow = true
      setTimeout(() => { this.toastShow = false }, 3000)
    },
    async handleLogin() {
      this.errorMessage = ''
      if (!this.username || !this.password) {
        this.errorMessage = '请输入用户名和密码'
        return
      }
      this.loading = true
      try {
        const data = await AuthAPI.login(this.username, this.password)
        if (data.code === 200) {
          this.showToast('登录成功！', 'success')
          setTimeout(() => { uni.reLaunch({ url: '/pages/home/index/index' }) }, 500)
        } else {
          this.errorMessage = data.message || '登录失败'
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
.login-page {
  display: flex;
  min-height: 100vh;
  background-color: #FFF5F7;
}

.brand-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 48rpx;
  background: linear-gradient(135deg, #FFF5F7 0%, #FF9EAA 100%);

  .brand-icon {
    width: 128rpx;
    height: 128rpx;
    background: white;
    border-radius: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 64rpx;
    margin-bottom: 32rpx;
    box-shadow: 0 20rpx 50rpx rgba(148, 70, 82, 0.15);
  }

  .brand-title {
    font-size: 48rpx;
    font-weight: 700;
    color: #1b1c1c;
    margin-bottom: 16rpx;
  }

  .brand-desc {
    font-size: 18rpx;
    color: #534344;
    text-align: center;
    line-height: 1.6;
    opacity: 0.7;
  }
}

.form-section {
  width: 100%;
  max-width: 440px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48rpx;
  background: white;
}

.mobile-logo {
  text-align: center;
  margin-bottom: 48rpx;

  .logo-icon {
    width: 64rpx;
    height: 64rpx;
    background: #ff9eaa;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    margin: 0 auto 16rpx;
  }

  h2 {
    font-size: 24rpx;
    font-weight: 700;
    color: #1b1c1c;
  }
}

.form-header {
  margin-bottom: 40rpx;

  .form-title {
    font-size: 28rpx;
    font-weight: 700;
    color: #1b1c1c;
    margin-bottom: 8rpx;
  }

  .form-subtitle {
    font-size: 14rpx;
    color: #534344;
    opacity: 0.6;
  }
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

.form-body {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.input-label {
  font-size: 12rpx;
  font-weight: 600;
  color: #534344;
  text-transform: uppercase;
}

.input-wrapper {
  display: flex;
  align-items: center;
  background: #f5f3f3;
  border-radius: 12rpx;
  padding: 0 16rpx;
  height: 88rpx;

  .input-icon {
    font-size: 20rpx;
    opacity: 0.4;
    margin-right: 12rpx;
  }

  .input-field {
    flex: 1;
    height: 88rpx;
    font-size: 16rpx;
    background: transparent;
    border: none;
    outline: none;
    color: #1b1c1c;
  }

  .password-toggle {
    padding: 8rpx;
    font-size: 20rpx;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8rpx 0;

  .remember-me {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 14rpx;
    color: #534344;
  }

  .forgot-link {
    font-size: 14rpx;
    color: #944652;
    font-weight: 600;
  }
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: #ff9eaa;
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 16rpx;
  font-weight: 600;
  margin-top: 16rpx;
  box-shadow: 0 10rpx 25rpx rgba(255, 158, 170, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-link {
  text-align: center;
  margin-top: 32rpx;

  text {
    font-size: 14rpx;
    color: #534344;
    opacity: 0.6;
  }

  .link-btn {
    color: #944652;
    font-weight: 700;
    margin-left: 4rpx;
  }
}

.toast {
  position: fixed;
  top: 20rpx;
  left: 50%;
  transform: translateX(-50%) translateY(-100rpx);
  padding: 12rpx 24rpx;
  border-radius: 12rpx;
  background: #dc2626;
  color: white;
  font-size: 14rpx;
  z-index: 9999;
  transition: transform 0.3s;

  &.toast-show {
    transform: translateX(-50%) translateY(0);
  }

  &.toast-success {
    background: #7DD87D;
  }
}
</style>