<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #fbf9f8;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  box-sizing: border-box;
}

/* 颜色变量 */
$primary: #944652;
$primary-container: #ff9eaa;
$income-green: #7DD87D;
$error: #ba1a1a;
$warning: #FF9800;
$surface-white: #FFFFFF;
$background-blush: #FFF5F7;
$on-surface: #1b1c1c;
$on-surface-variant: #534344;
$outline-variant: #d9c1c2;
</style>
