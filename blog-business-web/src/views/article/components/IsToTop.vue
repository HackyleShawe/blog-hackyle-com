<!--
  是否置顶的下拉选项
-->
<template>
  <el-dropdown :show-timeout="100" trigger="click">
    <el-button plain>
      <!--Draft：草稿；Release：发布-->
      {{ toTop ? 'ToTOP':'NoTop' }}
      <i class="el-icon-caret-bottom el-icon--right" />
    </el-button>
    <el-dropdown-menu slot="dropdown" class="no-padding">
      <el-dropdown-item>
        <el-radio-group v-model="toTop" style="padding: 10px;">
          <el-radio :label="true">
            Article To TOP
          </el-radio>
          <el-radio :label="false">
            Article No Top
          </el-radio>
        </el-radio-group>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  name: "IsToTop",
  props: {
    value: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    toTop: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  }
}
</script>
