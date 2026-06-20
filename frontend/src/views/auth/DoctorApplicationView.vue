<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { applyDoctor, getDepartments } from '../../api/auth'

const router = useRouter()
const loadingDepartments = ref(false)
const submitting = ref(false)
const submitted = ref(false)
const departments = ref([])
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  displayName: '',
  departmentId: null,
  phone: '',
  qualification: '',
  profile: '',
})

function errorMessage(error, fallback) {
  return error.response?.data?.message || error.message || fallback
}

async function loadDepartments() {
  loadingDepartments.value = true
  try {
    departments.value = await getDepartments()
  } catch (error) {
    ElMessage.error(errorMessage(error, '科室信息加载失败'))
  } finally {
    loadingDepartments.value = false
  }
}

async function submit() {
  if (form.username.trim().length < 3) {
    ElMessage.warning('用户名至少需要 3 位')
    return
  }
  if (form.password.length < 6) {
    ElMessage.warning('密码至少需要 6 位')
    return
  }
  if (form.password !== form.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  if (!form.displayName.trim()) {
    ElMessage.warning('请填写医生姓名')
    return
  }
  if (!form.departmentId) {
    ElMessage.warning('请选择申请科室')
    return
  }
  if (!/^1\d{10}$/.test(form.phone.trim())) {
    ElMessage.warning('请输入 11 位手机号')
    return
  }
  if (!form.qualification.trim()) {
    ElMessage.warning('请填写资质或执业信息')
    return
  }

  submitting.value = true
  try {
    await applyDoctor({
      username: form.username.trim(),
      password: form.password,
      displayName: form.displayName.trim(),
      departmentId: form.departmentId,
      phone: form.phone.trim(),
      qualification: form.qualification.trim(),
      profile: form.profile.trim(),
    })
    submitted.value = true
  } catch (error) {
    ElMessage.error(errorMessage(error, '申请提交失败，请稍后重试'))
  } finally {
    submitting.value = false
  }
}

onMounted(loadDepartments)
</script>

<template>
  <main class="doctor-application">
    <section class="admission-intro">
      <p class="eyebrow">DOCTOR ADMISSION</p>
      <h1>让专业判断，进入每一次真实问诊</h1>
      <p class="intro-copy">
        医生账号采用审核准入。平台会核验申请资料与所属科室，审核通过后即可进入医生工作台。
      </p>

      <ol class="admission-steps">
        <li>
          <span>01</span>
          <div><strong>提交资料</strong><small>填写账号、科室与执业信息</small></div>
        </li>
        <li>
          <span>02</span>
          <div><strong>管理员审核</strong><small>核验申请资料与平台准入资格</small></div>
        </li>
        <li>
          <span>03</span>
          <div><strong>进入工作台</strong><small>审核通过后使用申请账号登录</small></div>
        </li>
      </ol>
    </section>

    <section v-if="!submitted" class="application-card">
      <header>
        <p>医生入驻申请</p>
        <h2>创建医生申请</h2>
        <span>普通用户无需填写此表，请返回注册账号。</span>
      </header>

      <el-form label-position="top" @submit.prevent="submit">
        <div class="form-section">
          <div class="section-label"><span>01</span><strong>账号信息</strong></div>
          <div class="field-grid">
            <el-form-item label="登录用户名">
              <el-input v-model="form.username" autocomplete="username" placeholder="至少 3 位" />
            </el-form-item>
            <el-form-item label="医生姓名">
              <el-input v-model="form.displayName" autocomplete="name" placeholder="用于后台展示" />
            </el-form-item>
            <el-form-item label="登录密码">
              <el-input v-model="form.password" autocomplete="new-password" show-password type="password" placeholder="至少 6 位" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="form.confirmPassword" autocomplete="new-password" show-password type="password" placeholder="再次输入密码" />
            </el-form-item>
          </div>
        </div>

        <div class="form-section">
          <div class="section-label"><span>02</span><strong>执业信息</strong></div>
          <div class="field-grid">
            <el-form-item label="申请科室">
              <el-select
                v-model="form.departmentId"
                :loading="loadingDepartments"
                placeholder="请选择所属科室"
              >
                <el-option
                  v-for="department in departments"
                  :key="department.id"
                  :label="department.name"
                  :value="department.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" autocomplete="tel" maxlength="11" placeholder="请输入 11 位手机号" />
            </el-form-item>
          </div>
          <el-form-item label="资质或执业信息">
            <el-input
              v-model="form.qualification"
              maxlength="500"
              placeholder="例如：中医执业医师，执业年限与主要资质"
            />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="form.profile"
              :rows="3"
              maxlength="1000"
              placeholder="简要说明擅长方向，选填"
              resize="none"
              show-word-limit
              type="textarea"
            />
          </el-form-item>
        </div>

        <div class="form-actions">
          <button type="button" @click="router.push('/register')">返回普通注册</button>
          <el-button type="primary" :loading="submitting" @click="submit">提交入驻申请</el-button>
        </div>
      </el-form>
    </section>

    <section v-else class="application-card success-card">
      <span class="success-mark">✓</span>
      <p>申请已进入审核队列</p>
      <h2>资料提交成功</h2>
      <div>
        管理员审核通过后，你就可以使用刚才创建的用户名和密码登录医生工作台。
      </div>
      <el-button type="primary" @click="router.push('/login')">返回登录</el-button>
    </section>
  </main>
</template>

<style scoped>
.doctor-application {
  display: grid;
  width: min(1180px, calc(100vw - 68px));
  min-width: 980px;
  min-height: 690px;
  grid-template-columns: .78fr 1.22fr;
  margin: 24px auto 0;
  overflow: hidden;
  border: 1px solid rgb(47 74 60 / 12%);
  border-radius: 28px;
  background: white;
  box-shadow: 0 30px 80px rgb(43 66 54 / 14%);
}

.admission-intro {
  position: relative;
  padding: 62px 54px;
  overflow: hidden;
  background:
    radial-gradient(circle at 88% 12%, rgb(255 255 255 / 18%), transparent 24%),
    linear-gradient(148deg, #123f2c, #28684c);
  color: white;
}

.admission-intro::after {
  position: absolute;
  right: -90px;
  bottom: -120px;
  width: 320px;
  height: 320px;
  border: 1px solid rgb(255 255 255 / 13%);
  border-radius: 50%;
  content: "";
  box-shadow: 0 0 0 42px rgb(255 255 255 / 4%), 0 0 0 86px rgb(255 255 255 / 3%);
}

.eyebrow {
  margin: 0 0 20px;
  color: #f0b09f;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: .18em;
}

.admission-intro h1 {
  max-width: 360px;
  margin: 0;
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 42px;
  font-weight: 600;
  line-height: 1.45;
}

.intro-copy {
  max-width: 350px;
  margin: 24px 0 44px;
  color: rgb(255 255 255 / 72%);
  font-size: 14px;
  line-height: 1.9;
}

.admission-steps {
  position: relative;
  z-index: 1;
  display: grid;
  gap: 22px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.admission-steps li {
  display: flex;
  align-items: center;
  gap: 14px;
}

.admission-steps li > span {
  display: grid;
  width: 38px;
  height: 38px;
  flex: 0 0 auto;
  border: 1px solid rgb(255 255 255 / 25%);
  border-radius: 50%;
  color: #f0b09f;
  font-size: 10px;
  font-weight: 900;
  place-items: center;
}

.admission-steps strong,
.admission-steps small {
  display: block;
}

.admission-steps strong {
  font-size: 14px;
}

.admission-steps small {
  margin-top: 5px;
  color: rgb(255 255 255 / 60%);
  font-size: 11px;
}

.application-card {
  padding: 44px 52px;
  overflow-y: auto;
}

.application-card header > p,
.success-card > p {
  margin: 0 0 8px;
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 900;
  letter-spacing: .12em;
}

.application-card h2 {
  margin: 0;
  color: var(--color-ink);
  font-family: "Noto Serif SC", "STSong", serif;
  font-size: 34px;
}

.application-card header > span {
  display: block;
  margin-top: 10px;
  color: var(--color-text-muted);
  font-size: 12px;
}

.form-section {
  margin-top: 26px;
  padding-top: 22px;
  border-top: 1px solid rgb(47 95 72 / 10%);
}

.section-label {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 16px;
  color: var(--color-ink);
}

.section-label span {
  color: var(--color-cinnabar);
  font-size: 10px;
  font-weight: 900;
}

.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}

.el-select {
  width: 100%;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.form-actions > button {
  border: 0;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  font-size: 12px;
}

.form-actions .el-button {
  min-width: 148px;
  min-height: 44px;
  border-radius: 12px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 11px;
  box-shadow: 0 0 0 1px #d9e0dc inset;
}

.success-card {
  display: grid;
  align-content: center;
  justify-items: start;
}

.success-mark {
  display: grid;
  width: 64px;
  height: 64px;
  margin-bottom: 28px;
  border-radius: 20px;
  background: #e2efe7;
  color: #236043;
  font-size: 28px;
  font-weight: 900;
  place-items: center;
}

.success-card div {
  max-width: 480px;
  margin: 22px 0 30px;
  color: var(--color-text-muted);
  line-height: 1.9;
}
</style>
