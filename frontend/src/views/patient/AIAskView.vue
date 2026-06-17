<script setup>
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

import { askAI } from '../../api/content'
import {
  buildAIContext,
  createAssistantMessage,
  createConversation,
  createUserMessage,
  removeConversation,
  removeEmptyConversations,
  summarizeConversation,
} from '../../features/ai/session'

const STORAGE_KEY = 'tcm-ai-conversations'
const input = ref('')
const loading = ref(false)
const activeId = ref('')
const conversations = ref([])
const messagesRef = ref(null)
const examples = ['最近容易疲倦，日常作息可以怎么调整？', '春季饮食有哪些温和的养生建议？', '睡眠不安稳时可以注意哪些生活习惯？']

const activeConversation = computed(() =>
  conversations.value.find((conversation) => conversation.id === activeId.value) || conversations.value[0],
)

const activeMessages = computed(() => activeConversation.value?.messages || [])

function readConversations() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
  } catch {
    return []
  }
}

function saveConversations() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(conversations.value))
}

function ensureConversation(seed = '') {
  if (activeConversation.value) {
    return activeConversation.value
  }
  return createNewConversation(seed)
}

function createNewConversation(seed = '') {
  const conversation = createConversation(seed)
  conversations.value.unshift(conversation)
  activeId.value = conversation.id
  return conversation
}

function deleteConversation(id) {
  const result = removeConversation(conversations.value, id)
  conversations.value = result.conversations.length ? result.conversations : [createConversation()]
  activeId.value = result.activeId || conversations.value[0].id
}

function selectConversation(id) {
  activeId.value = id
}

function setExample(item) {
  input.value = item
}

function scrollToLatest() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function appendMessage(conversation, message) {
  conversation.messages.push(message)
  conversation.title = summarizeConversation(conversation)
  conversation.updatedAt = new Date().toISOString()
  scrollToLatest()
}

async function submit() {
  const question = input.value.trim()
  if (!question) return ElMessage.warning('请先输入想了解的问题')

  const conversation = ensureConversation(question)
  const context = buildAIContext(conversation.messages)
  appendMessage(conversation, createUserMessage(question))
  input.value = ''
  loading.value = true
  try {
    const answer = await askAI(question, context)
    appendMessage(conversation, createAssistantMessage(answer.answer, {
      fallback: answer.fallback,
      disclaimer: answer.disclaimer,
    }))
  } catch (error) {
    ElMessage.error(error.message || 'AI 问答暂时不可用')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const savedConversations = removeEmptyConversations(readConversations())
  conversations.value = savedConversations.length ? savedConversations : [createConversation()]
  activeId.value = conversations.value[0].id
  scrollToLatest()
})

watch(conversations, saveConversations, { deep: true })
</script>

<template>
  <section class="ai-page page-container">
    <header class="ai-hero">
      <div class="hero-copy">
        <p>日常养护参考</p>
        <h1>AI养护问答</h1>
        <span>围绕饮食、作息、运动与情志，提供温和谨慎的日常养护参考。</span>
      </div>
    </header>

    <div class="chat-shell">
      <aside class="conversation-panel">
        <button class="new-chat" type="button" @click="createNewConversation()">+ 新建对话</button>
        <div class="conversation-list">
          <div
            v-for="conversation in conversations"
            :key="conversation.id"
            :class="['conversation-item', { active: conversation.id === activeConversation?.id }]"
          >
            <button class="conversation-select" type="button" @click="selectConversation(conversation.id)">
              <strong>{{ summarizeConversation(conversation) }}</strong>
              <span>{{ conversation.messages.length ? `${conversation.messages.length} 条消息` : '尚未开始' }}</span>
            </button>
            <button class="delete-chat" type="button" aria-label="删除对话" @click.stop="deleteConversation(conversation.id)">删除</button>
          </div>
        </div>
      </aside>

      <section class="chat-panel">
        <div class="chat-heading">
          <div>
            <span>当前对话</span>
            <h2>{{ summarizeConversation(activeConversation || {}) }}</h2>
          </div>
          <small>回答仅用于健康科普参考，不能替代医生诊断。</small>
        </div>

        <div ref="messagesRef" class="messages">
          <div v-if="!activeMessages.length" class="empty-state">
            <p>可以从一个具体问题开始</p>
            <span>尽量说清楚场景、持续时间和你想追问的方向。</span>
            <div class="examples">
              <button v-for="item in examples" :key="item" type="button" @click="setExample(item)">{{ item }}</button>
            </div>
          </div>

          <article
            v-for="message in activeMessages"
            :key="message.id"
            :class="['message', message.role, { fallback: message.fallback }]"
          >
            <span>{{ message.role === 'user' ? '我' : (message.fallback ? '基础建议模式' : 'AI 参考建议') }}</span>
            <p>{{ message.content }}</p>
            <footer v-if="message.disclaimer">{{ message.disclaimer }}</footer>
          </article>

          <article v-if="loading" class="message assistant thinking">
            <span>AI 参考建议</span>
            <p>正在整理一份温和谨慎的回答……</p>
          </article>
        </div>

        <form class="composer" @submit.prevent="submit">
          <el-input
            id="ai-question"
            v-model="input"
            class="question-input"
            :maxlength="500"
            :rows="3"
            show-word-limit
            type="textarea"
            placeholder="继续追问，或描述一个新的养护问题"
            @keydown.ctrl.enter.prevent="submit"
          />
          <el-button type="primary" native-type="submit" :loading="loading">发送</el-button>
        </form>
      </section>
    </div>
    <div class="ai-safety-note">
      如出现胸痛、呼吸困难、意识异常、剧烈疼痛等紧急情况，请立即联系当地急救服务。
    </div>
  </section>
</template>

<style scoped>
.ai-page { padding-block: 22px 34px; }
.ai-hero { position: relative; overflow: hidden; display: flex; align-items: center; min-height: 150px; padding: 30px 34px; border-radius: 26px; background:
  radial-gradient(circle at 84% 18%, rgb(240 197 184 / 24%), transparent 34%),
  linear-gradient(115deg, rgba(17, 54, 40, .96), rgba(27, 88, 63, .68)); color: white; box-shadow: 0 18px 46px rgb(23 60 45 / 14%); }
.ai-hero::before { content: ""; position: absolute; inset: 18px 28px auto auto; width: 120px; height: 120px; border: 1px solid rgb(255 255 255 / 14%); border-radius: 50%; box-shadow: -54px 62px 0 rgb(255 255 255 / 5%); }
.hero-copy, .hero-note { position: relative; z-index: 1; }
.hero-copy p { margin: 0 0 8px; color: #f0c5b8; font-size: 12px; font-weight: 800; letter-spacing: .18em; }
.hero-copy h1 { margin: 0; font-family: "Noto Serif SC", "STSong", serif; font-size: clamp(2.4rem, 5vw, 4rem); letter-spacing: -.05em; line-height: 1.08; }
.hero-copy span { display: block; max-width: 520px; margin-top: 10px; font-size: 14px; line-height: 1.8; opacity: .88; }
.chat-shell { display: grid; grid-template-columns: 280px minmax(0, 1fr); gap: 18px; height: min(680px, calc(100vh - 300px)); min-height: 560px; margin-top: 18px; }
.conversation-panel, .chat-panel { border: 1px solid rgb(79 138 108 / 16%); border-radius: 28px; background: rgb(255 255 255 / 88%); box-shadow: 0 16px 44px rgb(23 60 45 / 8%); backdrop-filter: blur(14px); }
.conversation-panel { display: flex; flex-direction: column; gap: 14px; padding: 18px; overflow: hidden; }
.new-chat { padding: 13px 16px; border: 0; border-radius: 999px; background: var(--color-ink); color: white; cursor: pointer; font-weight: 900; box-shadow: 0 12px 26px rgb(23 60 45 / 15%); }
.conversation-list { display: grid; gap: 10px; overflow: auto; padding-right: 2px; }
.conversation-item { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 8px; align-items: center; padding: 8px; border: 1px solid transparent; border-radius: 18px; background: rgb(244 250 246 / 84%); transition: .18s ease; }
.conversation-item:hover, .conversation-item.active { border-color: rgb(79 138 108 / 24%); background: white; box-shadow: 0 10px 24px rgb(23 60 45 / 8%); }
.conversation-select { min-width: 0; padding: 6px; border: 0; background: transparent; color: var(--color-text-muted); cursor: pointer; text-align: left; }
.delete-chat { padding: 7px 9px; border: 1px solid rgb(201 81 61 / 14%); border-radius: 999px; background: rgb(255 250 246 / 76%); color: var(--color-cinnabar); cursor: pointer; font-size: 11px; font-weight: 800; opacity: .7; }
.delete-chat:hover { border-color: rgb(201 81 61 / 30%); background: #fff5ef; opacity: 1; }
.conversation-list strong { display: block; overflow: hidden; color: var(--color-ink); font-size: 14px; text-overflow: ellipsis; white-space: nowrap; }
.conversation-list span { display: block; margin-top: 7px; font-size: 11px; }
.chat-panel { display: grid; grid-template-rows: auto minmax(0, 1fr) auto; overflow: hidden; }
.chat-heading { display: flex; align-items: center; justify-content: space-between; gap: 18px; padding: 20px 24px; border-bottom: 1px solid rgb(79 138 108 / 12%); }
.chat-heading span { color: var(--color-cinnabar); font-size: 11px; font-weight: 900; letter-spacing: .14em; }
.chat-heading h2 { margin: 5px 0 0; color: var(--color-ink); font-family: "Noto Serif SC", "STSong", serif; font-size: 24px; letter-spacing: -.04em; }
.chat-heading small { max-width: 280px; color: var(--color-text-muted); font-size: 12px; line-height: 1.7; text-align: right; }
.messages { display: flex; flex-direction: column; gap: 14px; overflow-y: auto; padding: 22px 24px; background:
  radial-gradient(circle at 12% 10%, rgb(79 138 108 / 8%), transparent 24%),
  linear-gradient(180deg, rgb(250 253 250 / 72%), rgb(246 251 247 / 92%)); }
.empty-state { margin: auto; max-width: 560px; text-align: center; }
.empty-state p { margin: 0; color: var(--color-ink); font-family: "Noto Serif SC", "STSong", serif; font-size: 30px; font-weight: 800; }
.empty-state span { display: block; margin-top: 10px; color: var(--color-text-muted); line-height: 1.8; }
.examples { display: flex; flex-wrap: wrap; justify-content: center; gap: 9px; margin-top: 22px; }
.examples button { padding: 9px 13px; border: 1px solid rgb(79 138 108 / 18%); border-radius: 99px; background: rgb(255 255 255 / 86%); color: #506b60; cursor: pointer; font-size: 12px; font-weight: 700; transition: .18s ease; }
.examples button:hover { border-color: rgb(79 138 108 / 42%); color: var(--color-ink); transform: translateY(-1px); }
.message { width: min(76%, 680px); padding: 16px 18px; border: 1px solid rgb(79 138 108 / 13%); border-radius: 22px; background: white; box-shadow: 0 12px 30px rgb(23 60 45 / 7%); }
.message.user { align-self: flex-end; border-color: rgb(17 54 40 / 16%); background: var(--color-ink); color: white; }
.message.assistant { align-self: flex-start; }
.message.fallback { border-color: rgb(223 181 115 / 46%); background: #fffaf0; }
.message span { display: block; margin-bottom: 8px; color: var(--color-cinnabar); font-size: 11px; font-weight: 900; letter-spacing: .1em; }
.message.user span { color: #f0c5b8; }
.message p { margin: 0; white-space: pre-wrap; font-size: 14px; line-height: 1.95; }
.message.assistant p { color: #405e51; }
.message footer { margin-top: 14px; padding-top: 12px; border-top: 1px solid rgb(79 138 108 / 12%); color: var(--color-text-muted); font-size: 11px; line-height: 1.7; }
.thinking p { color: var(--color-text-muted); }
.composer { display: grid; grid-template-columns: minmax(0, 1fr) 96px; gap: 12px; align-items: end; padding: 16px 18px 18px; border-top: 1px solid rgb(79 138 108 / 12%); background: rgb(255 255 255 / 92%); }
.question-input :deep(.el-textarea__inner) { min-height: 82px !important; padding: 15px 18px; border: 0; border-radius: 20px; background: linear-gradient(145deg, #fbfdf9, #f1f8f4); box-shadow: 0 0 0 1px rgb(79 138 108 / 14%) inset; color: var(--color-ink); font-size: 14px; line-height: 1.8; resize: none; }
.question-input :deep(.el-input__count) { right: 14px; bottom: 8px; background: transparent; color: rgb(68 94 81 / 56%); }
.composer :deep(.el-button) { height: 48px; border: 0; border-radius: 999px; background: var(--color-ink); font-weight: 900; box-shadow: 0 12px 26px rgb(23 60 45 / 15%); }
.ai-safety-note { margin-top: 14px; padding: 12px 16px; border: 1px solid rgb(201 81 61 / 16%); border-radius: 18px; background: rgb(255 250 246 / 80%); color: var(--color-text-muted); font-size: 12px; line-height: 1.7; }
@media (max-width: 900px) { .chat-shell { grid-template-columns: 1fr; height: auto; } .conversation-panel { max-height: 260px; } .chat-panel { min-height: 620px; } .chat-heading { align-items: flex-start; flex-direction: column; } .chat-heading small { text-align: left; } .message { width: min(88%, 680px); } }
</style>
