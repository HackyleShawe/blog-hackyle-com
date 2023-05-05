<template>
  <div class="app-container">
    <el-form ref="form" :model="adminData" label-width="130px">
      <el-form-item label="Username">
        <el-input v-model="adminData.username" />
      </el-form-item>
      <el-form-item label="NewPassword">
        <el-input v-model="adminData.newPassword" />
      </el-form-item>
      <el-form-item label="ConfirmPassword">
        <el-input v-model="adminData.confirmPassword" />
      </el-form-item>
      <el-form-item label="NickName">
        <el-input v-model="adminData.nickName" />
      </el-form-item>
      <el-form-item label="RealName">
        <el-input v-model="adminData.realName" />
      </el-form-item>

      <el-form-item label="Description">
        <el-input v-model="adminData.description" />
      </el-form-item>
      <el-form-item label="Email">
        <el-input v-model="adminData.email" />
      </el-form-item>
      <el-form-item label="Phone">
        <el-input v-model="adminData.phone" />
      </el-form-item>
      <el-form-item label="Address">
        <el-input v-model="adminData.address" />
      </el-form-item>
      <el-form-item label="Username">
        <el-input v-model="adminData.username" />
      </el-form-item>

      <el-form-item label="UpdateTime">
        <el-input v-model="adminData.updateTime" disabled readonly/>
      </el-form-item>

      <el-form-item label="Avatar">
        <el-input v-model="adminData.avatar" />
      </el-form-item>
      <el-form-item label="Avatar">
        <el-avatar size="large" :src="adminData.avatar"></el-avatar>
      </el-form-item>

      <el-form-item label="Gender">
        <el-radio v-model="adminData.gender" :label="0">Woman</el-radio>
        <el-radio v-model="adminData.gender" :label="1">Man</el-radio>
        <el-radio v-model="adminData.gender" :label="3">Unknown</el-radio>
      </el-form-item>

      <el-form-item label="Lock">
        <el-radio v-model="adminData.locked" :label="true">YES</el-radio>
        <el-radio v-model="adminData.locked" :label="false">NO</el-radio>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">Update</el-button>
        <!--<el-button @click="onCancel">Cancel</el-button>-->
      </el-form-item>
    </el-form>
  </div>
</template>



<script>
import {getInfo, update} from '@/api/administrator'
import store from "@/store";

export default {
  name: "AdminInfo",
  data() {
    return {
      adminData: {
        id: 0,
        username: '',
        newPassword: '',
        confirmPassword: '',
        nickName: '',
        realName: '',
        description: '',
        email: '',
        phone: '',
        address: '',
        birthday: '',
        gender: '',
        avatar: '',
        locked: false,
        updateTime: '',
      }
    }
  },
  created() {
    this.getAdminInfo()
  },
  methods: {
    //携带token请求后端接口
    getAdminInfo() {
      getInfo(store.getters.token).then(res => {
        this.adminData = res.data
        //console.log(this.adminData)
      }).catch(err => {
        console.log("getAdminInfo error:", err)
      })
    },
    onSubmit() {
      if(this.adminData.newPassword !== this.adminData.confirmPassword) {
        this.$message.warning("The new password is not equals to the confirm password")
        return
      }
      if(this.adminData.newPassword != null && this.adminData.newPassword.length < 8) {
        this.$message.warning("The new password length can not be less than 8")
        return
      }
      let param = {
        data: this.adminData
      }
      update(param).then(res => {
        if(res.state) {
          this.$message.success('Updated!')
          this.logout() //立即注销
        } else {
          this.$message.error(res.message)
        }
      }).catch(err => {
        console.log("Update admin info error: ", err)
      })
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    }
    //onCancel() {
    //  this.$message({
    //    message: 'cancel!',
    //    type: 'warning'
    //  })
    //}
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

