<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

import { askAI } from '../../api/content'

const question = ref('')
const loading = ref(false)
const answer = ref(null)
const examples = ['最近容易疲倦，日常作息可以怎么调整？', '春季饮食有哪些温和的养生建议？', '睡眠不安稳时可以注意哪些生活习惯？']

async function submit() {
  if (!question.value.trim()) return ElMessage.warning('请先输入想了解的问题')
  loading.value = true
  try {
    answer.value = await askAI(question.value.trim())
  } catch (error) {
    ElMessage.error(error.message || 'AI 问答暂时不可用')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="ai-page page-container">
    <div class="ai-intro">
      <p>AI 养生助手</p>
      <h1>从一个具体问题开始</h1>
      <span>用于日常健康科普与生活方式建议，不能替代医生诊断、处方或紧急医疗服务。</span>
    </div>
    <section class="ask-card">
      <label for="ai-question">你想了解什么？</label>
      <el-input id="ai-question" v-model="question" :maxlength="500" :rows="6" show-word-limit type="textarea" placeholder="请描述一个具体的日常养生问题" />
      <div class="examples"><button v-for="item in examples" :key="item" type="button" @click="question = item">{{ item }}</button></div>
      <el-button type="primary" :loading="loading" @click="submit">获取养生建议</el-button>
    </section>
    <article v-if="answer" :class="['answer-card', { fallback: answer.fallback }]">
      <span>{{ answer.fallback ? '基础建议模式' : 'AI 回答' }}</span>
      <h2>给你的日常建议</h2>
      <p>{{ answer.answer }}</p>
      <footer>{{ answer.disclaimer }}</footer>
    </article>
  </section>
</template>

<style scoped>
.ai-page { display: grid; min-height: 66vh; grid-template-columns: .8fr 1.2fr; gap: 22px; padding-block: 48px; }
.ai-intro { padding: 34px; border-radius: var(--radius-lg); background: var(--color-ink); color: white; }
.ai-intro p { color: #a9d6bd; font-size: 11px; font-weight: 800; letter-spacing: .16em; }
.ai-intro h1 { margin: 28px 0; font-size: clamp(2.7rem, 6vw, 5rem); letter-spacing: -.07em; line-height: 1.05; }
.ai-intro span { color: rgb(255 255 255 / 65%); font-size: 13px; line-height: 1.9; }
.ask-card, .answer-card { padding: 30px; border: 1px solid var(--color-border); border-radius: var(--radius-lg); background: white; box-shadow: var(--shadow-card); }
.ask-card label { display: block; margin-bottom: 14px; font-size: 18px; font-weight: 800; }
.examples { display: flex; flex-wrap: wrap; gap: 7px; margin: 14px 0 20px; }
.examples button { padding: 8px 10px; border: 1px solid var(--color-border); border-radius: 99px; background: var(--color-mist); color: var(--color-text-muted); cursor: pointer; font-size: 11px; }
.answer-card { grid-column: 2; }
.answer-card.fallback { border-color: #dfb573; background: #fffaf0; }
.answer-card > span { color: var(--color-cinnabar); font-size: 10px; font-weight: 800; letter-spacing: .12em; }
.answer-card h2 { margin: 14px 0; }
.answer-card p { white-space: pre-wrap; line-height: 1.95; }
.answer-card footer { margin-top: 24px; padding-top: 14px; border-top: 1px solid var(--color-border); color: var(--color-text-muted); font-size: 11px; line-height: 1.7; }
@media (max-width: 850px) { .ai-page { grid-template-columns: 1fr; } .answer-card { grid-column: auto; } }
</style>
