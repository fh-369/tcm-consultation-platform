<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { createConsultation } from '../../api/consultation'

const router = useRouter()
const submitting = ref(false)
const form = reactive({
  patientName: '',
  age: null,
  gender: '',
  phone: '',
  symptoms: '',
  duration: '',
  allergyHistory: '',
  urgency: '普通',
  patientNote: '',
})

function errorMessage(error) {
  return error.response?.data?.message || error.message || '问诊提交失败，请稍后重试'
}

async function submit() {
  if (!form.patientName.trim()) {
    ElMessage.warning('请填写患者姓名')
    return
  }
  if (!form.symptoms.trim()) {
    ElMessage.warning('请描述主要症状')
    return
  }

  submitting.value = true
  try {
    await createConsultation(form)
    ElMessage.success('问诊单已提交')
    await router.push('/consultation/my')
  } catch (error) {
    ElMessage.error(errorMessage(error))
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="consultation-page page-container">
    <header class="page-heading">
      <div>
        <p>患者端问诊流程</p>
        <h1>建立问诊单</h1>
        <span>尽量清楚地记录症状与持续时间，便于医生判断处理顺序。</span>
      </div>
      <RouterLink to="/consultation/my">查看我的问诊</RouterLink>
    </header>

    <el-form class="consultation-form" label-position="top" @submit.prevent="submit">
      <section class="form-section">
        <div class="section-heading">
          <span>01</span>
          <div>
            <h2>患者信息</h2>
            <p>用于识别本次问诊对象。</p>
          </div>
        </div>

        <div class="field-grid">
          <el-form-item label="患者姓名（必填）">
            <el-input v-model="form.patientName" maxlength="100" placeholder="请输入患者姓名" />
          </el-form-item>
          <el-form-item label="年龄">
            <el-input-number v-model="form.age" :min="0" :max="150" controls-position="right" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" clearable placeholder="请选择">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" maxlength="20" placeholder="选填" />
          </el-form-item>
        </div>
      </section>

      <section class="form-section">
        <div class="section-heading">
          <span>02</span>
          <div>
            <h2>症状描述</h2>
            <p>重点说明不适部位、程度、持续时间和变化。</p>
          </div>
        </div>

        <el-form-item label="主要症状（必填）">
          <el-input
            v-model="form.symptoms"
            :rows="5"
            maxlength="2000"
            placeholder="例如：近三天反复胃痛，饭后更明显，伴轻微恶心……"
            show-word-limit
            type="textarea"
          />
        </el-form-item>
        <div class="field-grid">
          <el-form-item label="持续时间">
            <el-input v-model="form.duration" maxlength="100" placeholder="例如：约三天" />
          </el-form-item>
          <el-form-item label="紧急程度">
            <el-radio-group v-model="form.urgency">
              <el-radio-button label="普通" value="普通" />
              <el-radio-button label="紧急" value="紧急" />
              <el-radio-button label="非常紧急" value="非常紧急" />
            </el-radio-group>
          </el-form-item>
        </div>
      </section>

      <section class="form-section">
        <div class="section-heading">
          <span>03</span>
          <div>
            <h2>补充信息</h2>
            <p>过敏史和备注有助于医生了解整体情况。</p>
          </div>
        </div>

        <div class="field-grid">
          <el-form-item label="过敏史">
            <el-input v-model="form.allergyHistory" :rows="3" placeholder="选填" type="textarea" />
          </el-form-item>
          <el-form-item label="其他备注">
            <el-input v-model="form.patientNote" :rows="3" placeholder="选填" type="textarea" />
          </el-form-item>
        </div>
      </section>

      <div class="form-actions">
        <p>若出现危及生命的紧急情况，请立即联系当地急救服务。</p>
        <el-button type="primary" :loading="submitting" @click="submit">提交问诊单</el-button>
      </div>
    </el-form>
  </section>
</template>

<style scoped>
.consultation-page {
  padding-top: 42px;
}

.page-heading,
.form-actions {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 24px;
}

.page-heading p {
  margin: 0;
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.16em;
}

.page-heading h1 {
  margin: 10px 0;
  color: var(--color-ink);
  font-size: clamp(2.2rem, 5vw, 3.8rem);
  letter-spacing: -0.06em;
}

.page-heading span,
.section-heading p,
.form-actions p {
  color: var(--color-text-muted);
  font-size: 13px;
  line-height: 1.7;
}

.page-heading a {
  flex: 0 0 auto;
  padding: 12px 14px;
  border-bottom: 2px solid var(--color-cinnabar);
  color: var(--color-cinnabar);
  font-size: 12px;
  font-weight: 800;
}

.consultation-form {
  margin-top: 30px;
}

.form-section {
  display: grid;
  grid-template-columns: 230px minmax(0, 1fr);
  gap: 30px;
  margin-top: 14px;
  padding: 30px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: white;
  box-shadow: var(--shadow-card);
}

.section-heading {
  display: flex;
  gap: 14px;
}

.section-heading > span {
  color: var(--color-cinnabar);
  font-size: 11px;
  font-weight: 800;
}

.section-heading h2 {
  margin: 0;
  color: var(--color-ink);
  font-size: 19px;
}

.section-heading p {
  margin: 8px 0 0;
}

.field-grid {
  display: grid;
  grid-column: 2;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

.form-section > .el-form-item {
  grid-column: 2;
}

.form-actions {
  margin-top: 20px;
  padding: 22px 26px;
  border-radius: var(--radius-md);
  background: var(--color-ink);
}

.form-actions p {
  margin: 0;
  color: rgb(255 255 255 / 65%);
}

.form-actions .el-button {
  min-width: 150px;
}

@media (max-width: 850px) {
  .form-section {
    grid-template-columns: 1fr;
  }

  .field-grid,
  .form-section > .el-form-item {
    grid-column: 1;
  }
}

@media (max-width: 620px) {
  .page-heading,
  .form-actions {
    display: grid;
    align-items: start;
  }

  .form-section {
    padding: 22px 18px;
  }

  .field-grid {
    grid-template-columns: 1fr;
  }
}
</style>
